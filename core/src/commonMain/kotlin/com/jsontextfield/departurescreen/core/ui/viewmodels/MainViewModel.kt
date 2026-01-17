@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStopUseCase
import com.jsontextfield.departurescreen.core.domain.SetFavouriteStopUseCase
import com.jsontextfield.departurescreen.core.entities.Stop
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlin.time.ExperimentalTime

class MainViewModel(
    private val getSelectedStopUseCase: GetSelectedStopUseCase,
    private val setFavouriteStopUseCase: SetFavouriteStopUseCase,
    private val goTrainDataSource: ITransitRepository,
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
            getSelectedStopUseCase(),
            preferencesRepository.getFavouriteStops(),
        ) { selectedStop, favouriteStops ->
            val stopCodes = selectedStop?.code?.split(",") ?: emptySet()
            _uiState.update {
                it.copy(
                    selectedStop = selectedStop?.copy(
                        isFavourite = stopCodes.any { code -> code in favouriteStops }
                    )
                )
            }
        }.catch {
            _uiState.value = errorState
        }.launchIn(viewModelScope)
        loadData()

        getUnreadAlertsCount()
    }

    fun loadData() {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
                isRefreshing = false,
            )
        }
        viewModelScope.launch {
            getSelectedStopUseCase()
                .distinctUntilChanged()
                .catch {
                    _uiState.value = errorState
                    stop()
                }.collectLatest { selectedStop ->
                    _uiState.update {
                        it.copy(
                            status = Status.LOADING,
                            isRefreshing = false,
                            selectedStop = selectedStop,
                        )
                    }
                    fetchDepartureData()
                    startTimerJob()
                }
        }
    }

    fun refresh() {
        _uiState.update {
            it.copy(isRefreshing = true)
        }
        viewModelScope.launch {
            delay(1000)
            if (timeRemaining.value in 1001..<12000) {
                _timeRemaining.value = 1000
            }
            _uiState.update {
                it.copy(isRefreshing = false)
            }
        }
    }

    private fun startTimerJob() {
        timerJob = timerJob ?: viewModelScope.launch {
            while (isActive) {
                if (timeRemaining.value <= 1000) {
                    _timeRemaining.value = 0
                    fetchDepartureData()
                } else {
                    _timeRemaining.value -= 1000
                }
                delay(1000)
            }
        }.also {
            it.invokeOnCompletion {
                Logger.d("timerJob cancelled")
            }
        }
    }

    private fun fetchDepartureData() {
        val stop = uiState.value.selectedStop ?: return

        val stopCodes = stop.code.split(",")
        viewModelScope.launch {
            runCatching {
                stopCodes.flatMap { goTrainDataSource.getTrips(it) }
            }.onSuccess { trains ->
                val trainCodes = trains.map { it.code }.toSet() intersect uiState.value.visibleTrains
                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        isRefreshing = false,
                        _allTrips = trains,
                        visibleTrains = trainCodes,
                    )
                }
                // Reset the countdown timer only on a successful fetch.
                _timeRemaining.value = 20000
            }.onFailure { exception ->
                if (exception is IOException) {
                    _timeRemaining.value = 1000
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
        val trainCodes = uiState.value.allTrips.map { it.code }.toSet() intersect selectedTrains
        viewModelScope.launch {
            preferencesRepository.setVisibleTrains(trainCodes)
        }
    }

    fun setFavouriteStops(stop: Stop) {
        viewModelScope.launch {
            setFavouriteStopUseCase(stop)
        }
    }

    fun setSelectedStop(stopCode: String?) {
        viewModelScope.launch {
            preferencesRepository.setSelectedStopCode(stopCode ?: "UN")
        }
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    fun getUnreadAlertsCount() {
        combine(
            preferencesRepository.getReadAlerts(),
            goTrainDataSource.getServiceAlerts(),
            goTrainDataSource.getInformationAlerts(),
        ) { readAlerts, serviceAlertsList, informationAlertsList ->
            val count = (serviceAlertsList + informationAlertsList).count {
                it.id !in readAlerts
            }
            _uiState.update {
                it.copy(unreadAlertsCount = count)
            }
        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}

data class MainUIState(
    val status: Status = Status.LOADING,
    val selectedStop: Stop? = null,
    private val _allTrips: List<Trip> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
    val theme: ThemeMode = ThemeMode.DEFAULT,
    val isRefreshing: Boolean = false,
    val unreadAlertsCount: Int = 0,
) {
    val allTrips: List<Trip> = _allTrips.map { train ->
        train.copy(isVisible = train.code in visibleTrains || visibleTrains.isEmpty())
    }.sortedWith(
        when (sortMode) {
            SortMode.TIME -> compareBy { it.departureTime }
            SortMode.LINE -> compareBy({ it.code }, { it.destination })
        }
    )
}

private val errorState = MainUIState(
    status = Status.ERROR,
    isRefreshing = false,
)