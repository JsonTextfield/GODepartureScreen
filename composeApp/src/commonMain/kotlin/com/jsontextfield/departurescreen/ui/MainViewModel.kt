package com.jsontextfield.departurescreen.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.Alert
import com.jsontextfield.departurescreen.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.data.IPreferencesRepository
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
                    visibleTrains = preferencesRepository.getVisibleTrains() ?: emptySet(),
                    sortMode = preferencesRepository.getSortMode() ?: SortMode.TIME,
                    //theme = preferencesRepository.getTheme() ?: ThemeMode.DEFAULT,
                )
            }
        }
        timerJob = timerJob ?: viewModelScope.launch {
            while (true) {
                if (timeRemaining.value <= 0) {
                    runCatching {
                        _uiState.update { it.copy(allTrains = goTrainDataSource.getTrains()) }
                        setVisibleTrains(uiState.value.visibleTrains)
                        setSortMode(uiState.value.sortMode)
                        _timeRemaining.update { 20_000 }
                    }.onFailure { exception ->
                        if (exception is IOException) {
                            _timeRemaining.update { 1000 }
                        }
                    }
                } else {
                    delay(1000)
                    _timeRemaining.update { it - 1000 }
                }
            }
        }
        alertsJob = alertsJob ?: viewModelScope.launch {
            while (true) {
                runCatching {
                    _serviceAlerts.update { goTrainDataSource.getServiceAlerts() }
                    _informationAlerts.update { goTrainDataSource.getInformationAlerts() }
                    delay(60_000)
                }.onFailure { exception ->
                    if (exception is IOException) {
                        delay(1000)
                    }
                }
            }
        }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
            _uiState.update { it.copy(theme = theme) }
        }
    }

    fun setSortMode(mode: SortMode) {
        viewModelScope.launch {
            preferencesRepository.setSortMode(mode)
            _uiState.update { uiState ->
                uiState.copy(
                    sortMode = mode,
                    allTrains = uiState.allTrains.sortedWith(
                        when (mode) {
                            SortMode.TIME -> compareBy { it.departureTime }
                            SortMode.LINE -> compareBy({ it.code }, { it.destination })
                        }
                    ),
                )
            }
        }
    }

    fun setVisibleTrains(selectedTrains: Set<String>) {
        viewModelScope.launch {
            // Ensure that the visible trains are a subset of all trains
            val trainCodes = uiState.value.allTrains.map { it.code }.toSet() intersect selectedTrains
            preferencesRepository.setVisibleTrains(trainCodes)
            _uiState.update {
                it.copy(
                    visibleTrains = trainCodes,
                    allTrains = it.allTrains.map { train ->
                        train.copy(isVisible = train.code in trainCodes || trainCodes.isEmpty())
                    }
                )
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