package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Schedule
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class TripDetailsViewModel(
    private val preferencesRepository: IPreferencesRepository,
    private val goTrainDataSource: ITransitRepository,
    private val selectedStop: String,
    private val tripId: String,
    private val lineCode: String,
    private val destination: String,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripUIState> = MutableStateFlow(TripUIState())
    val uiState: StateFlow<TripUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    @OptIn(ExperimentalTime::class)
    fun loadData() {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
            )
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    lineCode = lineCode,
                    selectedStop = selectedStop,
                    destination = destination,
                )
            }
            val schedules = if (lineCode == "UP") {
                goTrainDataSource.getUPExpressTripSchedule(tripId)
            } else {
                goTrainDataSource.getTripDetails(tripId)?.stops
            } ?: emptyList()
            _uiState.update {
                it.copy(
                    status = Status.LOADED,
                    stops = schedules,
                )
            }
            combine(
                goTrainDataSource.getServiceAlerts(),
                goTrainDataSource.getInformationAlerts(),
                preferencesRepository.getTimeFormat(),
            ) { serviceAlerts, informationAlerts, timeFormat ->
                _uiState.update {
                    it.copy(
                        alerts = (serviceAlerts + informationAlerts)
                            .map { it.copy(isRead = true) }
                            .filter { alert ->
                                alert.affectedLines.any { line -> line == lineCode } ||
                                        alert.affectedStops.any { stop -> stop == selectedStop }
                            },
                        timeFormat = timeFormat,
                    )
                }
            }.collect()
        }
    }

    fun setSelectedStop(stopCode: String) {
        viewModelScope.launch {
            preferencesRepository.setSelectedStopCode(stopCode)
        }
    }
}

data class TripUIState(
    val status: Status = Status.LOADING,
    val lineCode: String = "",
    val selectedStop: String = "",
    val destination: String = "",
    val stops: List<Schedule> = emptyList(),
    val alerts: List<Alert> = emptyList(),
    val serviceGuarantee: String = "",
    val timeFormat: TimeFormat = TimeFormat.RELATIVE,
)