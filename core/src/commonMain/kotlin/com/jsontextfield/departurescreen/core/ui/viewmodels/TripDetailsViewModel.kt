package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
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
    private val stopId: String?,
    private val tripId: String?,
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
        if (stopId != null && tripId != null) {
            viewModelScope.launch {
                val trip = goTrainDataSource.getTrips(stopId).firstOrNull { it.id == tripId }
                val station = goTrainDataSource.getAllStations().firstOrNull { it.code == stopId }
                _uiState.update {
                    it.copy(
                        trip = trip,
                        currentStop = station,
                    )
                }
                val allStations = goTrainDataSource.getAllStations().associate {
                    it.code to it.name
                }
                val result = goTrainDataSource.getTripDetails(tripId)
                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        stops = result?.stops?.map { stop ->
                            allStations[stop]
                                ?: allStations.firstNotNullOfOrNull { (code, name) -> if (stop in code) name else null }
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
                                    alert.affectedLines.any { line -> line == trip?.code } ||
                                            alert.affectedStations.any { station -> station == stopId }
                                }
                        )
                    }
                }.collect()
            }
        }
    }
}

data class TripUIState(
    val status: Status = Status.LOADING,
    val trip: Trip? = null,
    val currentStop: Station? = null,
    val stops: List<String> = emptyList(),
    val alerts: List<Alert> = emptyList(),
)