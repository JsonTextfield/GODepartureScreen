package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Schedule
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class TripDetailsViewModel(
    private val preferencesRepository: IPreferencesRepository,
    private val transitRepository: ITransitRepository,
    private val selectedStop: String,
    private val stopCode: String,
    private val tripId: String,
    private val lineCode: String,
    private val destination: String,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripUIState> = MutableStateFlow(TripUIState())
    val uiState: StateFlow<TripUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
    fun loadData() {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
                lineCode = lineCode,
                selectedStop = selectedStop,
                destination = destination,
            )
        }
        viewModelScope.launch {
            preferencesRepository.getTimeFormat().collectLatest { timeFormat ->
                _uiState.update {
                    it.copy(
                        timeFormat = timeFormat,
                    )
                }
            }
        }
        preferencesRepository.getUseAlertsWithLinks().map { useLinks ->
            val alertsFlow = combine(
                transitRepository.getServiceAlerts(),
                transitRepository.getInformationAlerts(),
                transitRepository.getMarketingAlerts(),
            ) { service, info, marketing ->
                service + info + marketing
            }
            val allAlerts = if (useLinks) {
                combine(
                    alertsFlow,
                    transitRepository.getServiceUpdates("en"),
                ) { alerts, serviceUpdates ->
                    alerts + serviceUpdates
                }
            } else {
                alertsFlow
            }
            allAlerts.map { alerts ->
                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        alerts = alerts
                            .map { it.copy(isRead = true) }
                            .filter { alert ->
                                alert.affectedLines.any { line -> line == lineCode } ||
                                        alert.affectedStops.any { stop -> stop == selectedStop }
                            },
                    )
                }
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            try {
                if (lineCode == "UP") {
                    val moreTrips = transitRepository.getTrips(stopCode)
                        .filter { it.code == lineCode && it.id != tripId }
                        .take(4)

                    val schedules = transitRepository.getUPExpressTripSchedule(tripId)

                    _uiState.update {
                        it.copy(
                            status = Status.LOADED,
                            stops = schedules,
                            moreTrips = moreTrips,
                        )
                    }
                } else {
                    val tripDetails = transitRepository.getTripDetails(tripId, stopCode)
                    val sameDirectionTripNumbers = transitRepository.getMoreTrips(tripId, stopCode)
                    val moreTrips = transitRepository.getTrips(stopCode)
                        .filter { trip -> trip.code == lineCode && trip.id != tripId && trip.id in sameDirectionTripNumbers }
                        .take(4)

                    _uiState.update {
                        it.copy(
                            status = Status.LOADED,
                            stops = tripDetails?.stops.orEmpty(),
                            moreTrips = moreTrips,
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        status = Status.ERROR,
                    )
                }
            }
        }
    }

    fun setSelectedStop(stopName: String) {
        viewModelScope.launch {
            preferencesRepository.setSelectedStop(stopName)
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
    val moreTrips: List<Trip> = emptyList(),
)