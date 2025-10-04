package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.IOException

class MainViewModel(
    private val departureScreenUseCase: DepartureScreenUseCase,
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<MainUIState> = MutableStateFlow(MainUIState())
    val uiState: StateFlow<MainUIState> = _uiState.asStateFlow()

    private val _timeRemaining: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private var timerJob: Job? = null

    init {
        combine(
            preferencesRepository.getVisibleTrains(),
            preferencesRepository.getSortMode(),
            preferencesRepository.getTheme(),
        ) { visibleTrains, sortMode, theme ->
            _uiState.update {
                it.copy(
                    visibleTrains = visibleTrains,
                    sortMode = sortMode,
                    theme = theme,
                )
            }
        }.launchIn(viewModelScope)

        combine(
            departureScreenUseCase.getSelectedStation(),
            preferencesRepository.getFavouriteStations(),
        ) { selectedStation, favouriteStations ->
            _uiState.update {
                it.copy(
                    selectedStation = selectedStation?.copy(
                        isFavourite = selectedStation.codes.any { code -> code in favouriteStations }
                    )
                )
            }
        }.launchIn(viewModelScope)

        departureScreenUseCase.getSelectedStation()
            .distinctUntilChanged()
            .onEach { selectedStation ->
                if (selectedStation != null) {
                    fetchDepartureData(selectedStation)
                } else {
                    // Handle case where no station is selected
                    _uiState.update {
                        it.copy(
                            status = Status.LOADED,
                            _allTrips = emptyList(),
                        )
                    }
                }
            }
            .catch { _ ->
                _uiState.update { it.copy(status = Status.ERROR) }
            }.launchIn(viewModelScope)

        startTimerJob()
    }

    fun loadData() {
        if (uiState.value.status == Status.ERROR) {
            _uiState.update {
                it.copy(status = Status.LOADING)
            }
        }
        uiState.value.selectedStation?.let(::fetchDepartureData)
    }

    fun refresh() {
        _uiState.update {
            it.copy(isRefreshing = true)
        }
        viewModelScope.launch {
            delay(1000)
            if (timeRemaining.value < 12000) {
                _timeRemaining.update { 1000 }
            }
            _uiState.update {
                it.copy(isRefreshing = false)
            }
        }
    }

    private fun startTimerJob() {
        timerJob = timerJob ?: viewModelScope.launch {
            while (true) {
                delay(1000)
                if (timeRemaining.value <= 1000) {
                    _timeRemaining.update { 0 }
                    uiState.value.selectedStation?.let(::fetchDepartureData) ?: _timeRemaining.update { 20000 }
                } else {
                    _timeRemaining.update { it - 1000 }
                }
            }
        }
    }

    private fun fetchDepartureData(station: CombinedStation) {
        viewModelScope.launch {
            runCatching {
                val stationCodes = station.codes
                stationCodes.flatMap { goTrainDataSource.getTrains(it) }
            }.onSuccess { trains ->
                val trainCodes = trains.map { it.code }.toSet() intersect _uiState.value.visibleTrains
                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        _allTrips = trains,
                        visibleTrains = trainCodes,
                    )
                }
                // Reset the countdown timer only on a successful fetch.
                _timeRemaining.update { 20000 }
            }.onFailure { exception ->
                if (exception is IOException) {
                    _timeRemaining.update { 1000 }
                }
            }
        }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }

    fun setSortMode(mode: SortMode) {
        viewModelScope.launch {
            preferencesRepository.setSortMode(mode)
        }
    }

    fun setVisibleTrains(selectedTrains: Set<String>) {
        // Ensure that the visible trains are a subset of all trains
        val trainCodes = _uiState.value.allTrips.map { it.code }.toSet() intersect selectedTrains
        viewModelScope.launch {
            preferencesRepository.setVisibleTrains(trainCodes)
        }
    }

    fun setFavouriteStations(station: CombinedStation) {
        viewModelScope.launch {
            departureScreenUseCase.setFavouriteStations(station)
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}

data class MainUIState(
    val status: Status = Status.LOADING,
    val selectedStation: CombinedStation? = null,
    private val _allTrips: List<Trip> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
    val theme: ThemeMode = ThemeMode.DEFAULT,
    val isRefreshing: Boolean = false,
) {
    val allTrips: List<Trip>
        get() {
            return _allTrips.map { train ->
                train.copy(isVisible = train.code in visibleTrains || visibleTrains.isEmpty())
            }.sortedWith(
                when (sortMode) {
                    SortMode.TIME -> compareBy { it.departureTime }
                    SortMode.LINE -> compareBy({ it.code }, { it.destination })
                }
            )
        }
}