package com.jsontextfield.departurescreen.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.data.IPreferencesRepository
import com.jsontextfield.departurescreen.entities.Alert
import com.jsontextfield.departurescreen.entities.Station
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

    private var timerJob: Job? = null
    private var alertsJob: Job? = null

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
        viewModelScope.launch {
            runCatching {
                val allStations = goTrainDataSource.getAllStations()
                val selectedStation = uiState.value.selectedStation
                    ?: allStations.firstOrNull {
                        it.code == preferencesRepository.getSelectedStationCode()
                    } ?: allStations.firstOrNull {
                        it.code == "UN"
                    } ?: allStations.firstOrNull()
                _uiState.update { uiState ->
                    uiState.copy(
                        allStations = allStations
                            .filter { "Station" in it.type }
                            .map {
                                it.copy(isFavourite = it.code in uiState.favouriteStations)
                            }
                            .sortedWith(
                                compareByDescending<Station> { it.isFavourite }
                                    .thenBy { it.code != "UN" }
                                    .thenBy { it.name }
                            ),
                        selectedStation = selectedStation,
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(status = Status.ERROR)
                }
            }.onSuccess {
                startTimerJob()
                startAlertsJob()
            }
        }
    }

    private fun startTimerJob() {
        timerJob = timerJob ?: viewModelScope.launch {
            while (true) {
                if (timeRemaining.value <= 0) {
                    uiState.value.selectedStation?.code?.let { stationCode ->
                        runCatching {
                            goTrainDataSource.getTrains(stationCode)
                        }.onFailure { exception ->
                            if (exception is IOException) {
                                _timeRemaining.update { 1000 }
                            }
                        }.onSuccess { trains ->
                            val trainCodes = trains.map { it.code }.toSet() intersect uiState.value.visibleTrains
                            _uiState.update {
                                it.copy(
                                    status = Status.LOADED,
                                    _allTrains = trains,
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

    private fun startAlertsJob() {
        alertsJob = alertsJob ?: viewModelScope.launch {
            while (true) {
                runCatching {
                    val selectedStationCode = uiState.value.selectedStation?.code
                    val allTrainCodes = uiState.value.allTrains.map { it.code }.toSet()
                    _serviceAlerts.update {
                        goTrainDataSource.getServiceAlerts().filter {
                            (selectedStationCode in it.affectedStations || it.affectedStations.isEmpty())
                                    &&
                                    ((allTrainCodes intersect it.affectedLines).isNotEmpty() || it.affectedLines.isEmpty())
                        }
                    }
                    _informationAlerts.update {
                        goTrainDataSource.getInformationAlerts().filter {
                            (selectedStationCode in it.affectedStations || it.affectedStations.isEmpty())
                                    &&
                                    ((allTrainCodes intersect it.affectedLines).isNotEmpty() || it.affectedLines.isEmpty())
                        }
                    }
                }.onFailure { exception ->
                    if (exception is IOException) {
                        delay(1000)
                    }
                }.onSuccess {
                    delay(60_000)
                }
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
        val trainCodes = uiState.value.allTrains.map { it.code }.toSet() intersect selectedTrains
        _uiState.update { it.copy(visibleTrains = trainCodes) }
        viewModelScope.launch {
            preferencesRepository.setVisibleTrains(trainCodes)
        }
    }

    fun setSelectedStation(station: Station) {
        _uiState.update { it.copy(selectedStation = station) }
        viewModelScope.launch {
            preferencesRepository.setSelectedStationCode(station.code)
            runCatching {
                goTrainDataSource.getTrains(station.code)
            }.onSuccess { trains ->
                _uiState.update { it.copy(_allTrains = trains) }
                setVisibleTrains(uiState.value.visibleTrains)
            }.onFailure {
                _uiState.update { it.copy(_allTrains = emptyList()) }
            }
        }
    }

    fun setFavouriteStations() {
        uiState.value.selectedStation?.let { selectedStation ->
            val stations = uiState.value.favouriteStations
            val updatedStations = if (selectedStation.code in stations) {
                stations - selectedStation.code
            } else {
                stations + selectedStation.code
            }
            _uiState.update {
                it.copy(
                    allStations = it.allStations.map { station ->
                        station.copy(isFavourite = station.code in updatedStations)
                    }.sortedWith(
                        compareByDescending<Station> { it.isFavourite }
                            .thenBy { it.code != "UN" }
                            .thenBy { it.name }
                    ),
                    favouriteStations = updatedStations,
                    selectedStation = selectedStation.copy(isFavourite = !selectedStation.isFavourite)
                )
            }
            viewModelScope.launch {
                preferencesRepository.setFavouriteStations(updatedStations)
            }
        }
    }

    fun showAlertsScreen() {
        showAlerts = !showAlerts
    }

    fun stop() {
        timerJob?.cancel()
        timerJob = null
        alertsJob?.cancel()
        alertsJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}