package com.jsontextfield.departurescreen.core.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.entities.toCombinedStation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.IOException

class MainViewModel(
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _timeRemaining: MutableStateFlow<Int> = MutableStateFlow(0)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()

    private val _informationAlerts: MutableStateFlow<List<Alert>> = MutableStateFlow(emptyList())
    val informationAlerts: StateFlow<List<Alert>> = _informationAlerts.asStateFlow()

    private val _serviceAlerts: MutableStateFlow<List<Alert>> = MutableStateFlow(emptyList())
    val serviceAlerts: StateFlow<List<Alert>> = _serviceAlerts.asStateFlow()

    var showAlerts: Boolean by mutableStateOf(false)
    var showStationMenu: Boolean by mutableStateOf(false)

    private var timerJob: Job? = null

    private val stationComparator = compareByDescending<CombinedStation> { it.isFavourite }
        .thenBy { "UN" !in it.codes && "02300" !in it.codes }
        .thenBy { it.name }

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    favouriteStations = preferencesRepository.getFavouriteStations() ?: emptySet(),
                    visibleTrains = preferencesRepository.getVisibleTrains() ?: emptySet(),
                    sortMode = preferencesRepository.getSortMode() ?: SortMode.TIME,
                    //theme = preferencesRepository.getTheme() ?: ThemeMode.DEFAULT,
                )
            }
        }
        loadData()
    }

    fun loadData() {
        _uiState.update {
            it.copy(status = Status.LOADING)
        }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val allStations = goTrainDataSource.getAllStations()
                    .map { it.copy(isFavourite = it.code in uiState.value.favouriteStations) }
                    .groupBy { it.name }
                    .map { it.value.toCombinedStation() }
                    .sortedWith(stationComparator)
                val selectedStation = uiState.value.selectedStation
                    ?: allStations.firstOrNull {
                        preferencesRepository.getSelectedStationCode() in it.codes
                    } ?: allStations.firstOrNull {
                        "UN" in it.codes
                    } ?: allStations.firstOrNull()
                _uiState.update { uiState ->
                    uiState.copy(
                        allStations = allStations,
                        selectedStation = selectedStation,
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(status = Status.ERROR)
                }
            }.onSuccess {
                startTimerJob()
                loadAlerts()
            }
        }
    }

    fun refresh() {
        _uiState.update {
            it.copy(isRefreshing = true)
        }
        viewModelScope.launch {
            delay(1000)
            if (timeRemaining.value < 16000) {
                _timeRemaining.update { 1000 }
            }
            _uiState.update {
                it.copy(isRefreshing = false)
            }
        }
    }

    private fun startTimerJob() {
        timerJob = timerJob ?: viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                if (timeRemaining.value <= 0) {
                    uiState.value.selectedStation?.codes?.let { stationCodes ->
                        runCatching {
                            stationCodes.map {
                                goTrainDataSource.getTrains(it)
                            }.flatten()
                        }.onFailure { exception ->
                            if (exception is IOException) {
                                _timeRemaining.update { 1000 }
                            }
                        }.onSuccess { trains ->
                            val trainCodes = trains.map { it.code }.toSet() intersect uiState.value.visibleTrains
                            _uiState.update {
                                it.copy(
                                    status = Status.LOADED,
                                    _allTrips = trains,
                                    visibleTrains = trainCodes,
                                )
                            }
                            _timeRemaining.update { 20_000 }
                        }
                    }
                } else {
                    delay(1000)
                    _timeRemaining.update { it - 1000 }
                }
            }
        }
    }

    fun loadAlerts() {
        _uiState.update {
            it.copy(isAlertsRefreshing = true)
        }
        viewModelScope.launch {
            delay(1000)
            runCatching {
                val selectedStationCodes = uiState.value.selectedStation?.codes ?: emptySet()
                val allTrainCodes = uiState.value.allTrips.map { it.code }.toSet()
                _serviceAlerts.update {
                    goTrainDataSource.getServiceAlerts().filter {
                        (selectedStationCodes.any { code -> code in it.affectedStations } || it.affectedStations.isEmpty())
                                &&
                                (allTrainCodes.any { code -> code in it.affectedLines } || it.affectedLines.isEmpty())
                    }
                }
                _informationAlerts.update {
                    goTrainDataSource.getInformationAlerts().filter {
                        (selectedStationCodes.any { code -> code in it.affectedStations } || it.affectedStations.isEmpty())
                                &&
                                (allTrainCodes.any { code -> code in it.affectedLines } || it.affectedLines.isEmpty())
                    }
                }
            }
            _uiState.update {
                it.copy(isAlertsRefreshing = false)
            }
        }
    }

    fun setTheme(theme: ThemeMode) {
        _uiState.update { it.copy(theme = theme) }
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }

    fun setSortMode(mode: SortMode) {
        _uiState.update { it.copy(sortMode = mode) }
        viewModelScope.launch {
            preferencesRepository.setSortMode(mode)
        }
    }

    fun setVisibleTrains(selectedTrains: Set<String>) {
        // Ensure that the visible trains are a subset of all trains
        val trainCodes = uiState.value.allTrips.map { it.code }.toSet() intersect selectedTrains
        _uiState.update { it.copy(visibleTrains = trainCodes) }
        viewModelScope.launch {
            preferencesRepository.setVisibleTrains(trainCodes)
        }
    }

    fun setSelectedStation(station: CombinedStation) {
        _uiState.update { it.copy(selectedStation = station) }
        _timeRemaining.update { 0 }
        loadAlerts()
        viewModelScope.launch {
            preferencesRepository.setSelectedStationCode(station.codes.first())
        }
    }

    fun setFavouriteStations() {
        uiState.value.selectedStation?.let { selectedStation ->
            val stations = uiState.value.favouriteStations
            val updatedStations = if (selectedStation.codes.any { it in stations }) {
                stations - selectedStation.codes
            } else {
                stations + selectedStation.codes
            }
            _uiState.update {
                it.copy(
                    allStations = it.allStations.map { station ->
                        station.copy(isFavourite = station.codes.any { code -> code in updatedStations })
                    }.sortedWith(stationComparator),
                    favouriteStations = updatedStations,
                    selectedStation = selectedStation.copy(isFavourite = !selectedStation.isFavourite)
                )
            }
            viewModelScope.launch {
                preferencesRepository.setFavouriteStations(updatedStations)
            }
        }
    }

    fun setFavouriteStations(station: CombinedStation) {
        val stations = uiState.value.favouriteStations
        val updatedStations = if (station.codes.any { it in stations }) {
            stations - station.codes
        } else {
            stations + station.codes
        }
        _uiState.update {
            it.copy(
                allStations = it.allStations.map { station ->
                    station.copy(isFavourite = station.codes.any { code -> code in updatedStations })
                }.sortedWith(stationComparator),
                favouriteStations = updatedStations,
            )
        }
        viewModelScope.launch {
            preferencesRepository.setFavouriteStations(updatedStations)
        }
    }

    fun onBackPressed() {
        if (showAlerts) {
            showAlerts = false
        } else if (showStationMenu) {
            showStationMenu = false
        }
    }

    fun showAlertsScreen() {
        showAlerts = true
    }

    fun showStationMenu() {
        showStationMenu = true
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