package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.TripDetails
import com.jsontextfield.departurescreen.core.ui.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class TripDetailsViewModel(
    private val goTrainDataSource: IGoTrainDataSource,
    private val selectedStop: String,
    private val tripId: String,
    private val lineCode: String,
    private val destination: String,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripUIState> = MutableStateFlow(TripUIState())
    val uiState: StateFlow<TripUIState> = _uiState.asStateFlow()

    private val upExpressStations = listOf("UN", "BL", "MD", "WE", "PA")

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
            val allStops = goTrainDataSource.getAllStops().associate {
                it.code to it.name
            }
            val result = if (lineCode == "UP") {
                TripDetails(
                    id = tripId,
                    stops = if (destination == "Union Station") {
                        upExpressStations.reversed()
                    } else {
                        upExpressStations
                    }
                )
            } else {
                goTrainDataSource.getTripDetails(tripId)
            }
            _uiState.update {
                it.copy(
                    status = Status.LOADED,
                    stops = result?.stops?.map { stop ->
                        allStops[stop]
                            ?: allStops.firstNotNullOfOrNull { (code, name) -> if (stop in code) name else null }
                            ?: stop
                    }.orEmpty(),
                )
            }
            combine(
                goTrainDataSource.getServiceAlerts(),
                goTrainDataSource.getInformationAlerts(),
            ) { serviceAlerts, informationAlerts ->
                _uiState.update {
                    it.copy(
                        alerts = (serviceAlerts + informationAlerts)
                            .map { it.copy(isRead = true) }
                            .filter { alert ->
                                alert.affectedLines.any { line -> line == lineCode } ||
                                        alert.affectedStops.any { stop -> stop == selectedStop }
                            }
                    )
                }
            }.collect()
        }
    }
}

data class TripUIState(
    val status: Status = Status.LOADING,
    val lineCode: String = "",
    val selectedStop: String = "",
    val destination: String = "",
    val stops: List<String> = emptyList(),
    val alerts: List<Alert> = emptyList(),
)