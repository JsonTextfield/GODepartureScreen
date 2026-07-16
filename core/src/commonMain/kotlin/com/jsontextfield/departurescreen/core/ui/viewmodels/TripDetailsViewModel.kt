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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update {
            it.copy(
                status = if (it.status == Status.LOADING) Status.ERROR else Status.LOADED,
            )
        }
    }

    init {
        loadData()
    }

    fun loadData() {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
                lineCode = lineCode,
                selectedStop = selectedStop,
                destination = destination,
            )
        }

        preferencesRepository.getTimeFormat().map { timeFormat ->
            _uiState.update {
                it.copy(
                    timeFormat = timeFormat,
                )
            }
        }.launchIn(viewModelScope)

        loadAlerts()
        loadStops()
        loadMoreTrips()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadAlerts() {
        preferencesRepository.getUseAlertsWithLinks().flatMapLatest { useLinks ->
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
                val filteredAlerts = withContext(Dispatchers.IO) {
                    alerts
                        .map { it.copy(isRead = true) }
                        .filter { alert ->
                            alert.affectedLines.any { line -> line == lineCode } ||
                                    alert.affectedStops.any { stop -> stop == selectedStop }
                        }
                }
                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        alerts = filteredAlerts,
                    )
                }
            }
        }.catch {
            _uiState.update {
                it.copy(
                    status = if (it.status == Status.LOADING) Status.ERROR else Status.LOADED,
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadStops() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val schedules = withContext(Dispatchers.IO) {
                if (lineCode == "UP") {
                    transitRepository.getUPExpressTripSchedule(tripId)
                } else {
                    transitRepository.getTripDetails(tripId, stopCode)?.stops.orEmpty()
                }
            }
            _uiState.update {
                it.copy(
                    status = Status.LOADED,
                    stops = schedules,
                )
            }
        }
    }

    private fun loadMoreTrips() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val moreTrips = withContext(Dispatchers.IO) {
                if (lineCode == "UP") {
                    transitRepository.getTrips(stopCode)
                        .filter { trip -> trip.code == lineCode && trip.id != tripId }
                } else {
                    val sameDirectionTripNumbers = transitRepository.getMoreTrips(tripId, stopCode)
                    transitRepository.getTrips(stopCode)
                        .filter { trip -> trip.code == lineCode && trip.id != tripId && trip.id in sameDirectionTripNumbers }
                }
            }
            _uiState.update {
                it.copy(
                    status = Status.LOADED,
                    moreTrips = moreTrips,
                )
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