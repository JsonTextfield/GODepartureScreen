package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Schedule
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.network.FeatureFlagApi
import com.jsontextfield.departurescreen.core.network.isAdEnabled
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripDetailsViewModel(
    private val preferencesRepository: IPreferencesRepository,
    private val transitRepository: ITransitRepository,
    private val featureFlagApi: FeatureFlagApi,
    private val selectedStop: String,
    private val stopCode: String,
    private val tripId: String,
    private val lineCode: String,
    private val destination: String,
) : ViewModel() {
    private val _uiState: MutableStateFlow<TripUIState> = MutableStateFlow(TripUIState())
    val uiState: StateFlow<TripUIState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isAdEnabled = try {
                isAdEnabled(featureFlagApi)
            } catch (_: Exception) {
                false
            }
            _uiState.update {
                it.copy(
                    isAdEnabled = isAdEnabled
                )
            }
        }
        loadData()
    }

    fun loadData() {
        _uiState.update {
            it.copy(
                stopsStatus = Status.LOADING,
                moreTripsStatus = Status.LOADING,
                alertsStatus = Status.LOADING,
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

        loadStops()
        loadMoreTrips()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadAlerts(language: String = "en") {
        _uiState.update {
            it.copy(
                alertsStatus = Status.LOADING,
            )
        }
        transitRepository.getAllAlerts(language).map { alerts ->
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
                    alertsStatus = Status.LOADED,
                    alerts = filteredAlerts,
                )
            }
        }.catch {
            _uiState.update {
                it.copy(
                    alertsStatus = Status.ERROR,
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun loadStops() {
        _uiState.update {
            it.copy(
                stopsStatus = Status.LOADING,
            )
        }
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            _uiState.update {
                it.copy(
                    stopsStatus = Status.ERROR,
                )
            }
        }) {
            val schedules = withContext(Dispatchers.IO) {
                if (lineCode == "UP") {
                    transitRepository.getUPExpressTripSchedule(tripId)
                } else {
                    transitRepository.getTripDetails(tripId, stopCode)?.stops.orEmpty()
                }
            }
            _uiState.update {
                it.copy(
                    stopsStatus = Status.LOADED,
                    stops = schedules,
                )
            }
        }
    }

    private fun loadMoreTrips() {
        _uiState.update {
            it.copy(
                moreTripsStatus = Status.LOADING,
            )
        }
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            _uiState.update {
                it.copy(
                    moreTripsStatus = Status.ERROR,
                )
            }
        }) {
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
                    moreTripsStatus = Status.LOADED,
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
    val alertsStatus: Status = Status.LOADING,
    val stopsStatus: Status = Status.LOADING,
    val moreTripsStatus: Status = Status.LOADING,
    val lineCode: String = "",
    val selectedStop: String = "",
    val destination: String = "",
    val stops: List<Schedule> = emptyList(),
    val alerts: List<Alert> = emptyList(),
    val serviceGuarantee: String = "",
    val timeFormat: TimeFormat = TimeFormat.RELATIVE,
    val moreTrips: List<Trip> = emptyList(),
    val isAdEnabled: Boolean = false,
) {
    val status: Status = when {
        alertsStatus == Status.ERROR && stopsStatus == Status.ERROR && moreTripsStatus == Status.ERROR -> Status.ERROR
        alertsStatus == Status.LOADING && stopsStatus == Status.LOADING && moreTripsStatus == Status.LOADING -> Status.LOADING
        else -> Status.LOADED
    }
}