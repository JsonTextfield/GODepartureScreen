package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.domain.GetSelectedStationUseCase
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val getSelectedStationUseCase: GetSelectedStationUseCase,
    private val goTrainDataSource: IGoTrainDataSource,
) : ViewModel() {
    private val _uiState: MutableStateFlow<AlertsUIState> = MutableStateFlow(AlertsUIState())
    val uiState: StateFlow<AlertsUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
                isRefreshing = false,
            )
        }
        loadAlerts()
    }

    fun refresh() {
        _uiState.update {
            it.copy(isRefreshing = true)
        }
        loadAlerts()
    }

    private fun loadAlerts() {
        viewModelScope.launch {
            getSelectedStationUseCase().catch {
                _uiState.update {
                    it.copy(
                        status = Status.ERROR,
                        isRefreshing = false,
                    )
                }
            }.collectLatest { selectedStation ->
                runCatching {
                    val selectedStationCodes = selectedStation?.code?.split(",")?.toSet().orEmpty()
                    val allTrainCodes = selectedStationCodes
                        .flatMap {
                            goTrainDataSource.getTrips(it).map { it.code }
                        }.toSet()

                    val predicate: (Alert) -> Boolean = { alert: Alert ->
                        (selectedStationCodes.any { code -> code in alert.affectedStations } || alert.affectedStations.isEmpty())
                                &&
                                (allTrainCodes.any { code -> code in alert.affectedLines } || alert.affectedLines.isEmpty())
                    }
                    predicate
                }.onSuccess { predicate ->
                    combine(
                        goTrainDataSource.getServiceAlerts(),
                        goTrainDataSource.getInformationAlerts(),
                    ) { serviceAlertsList, informationAlertsList ->
                        val filteredServiceAlerts = serviceAlertsList.filter(predicate)
                        val filteredInformationAlerts = informationAlertsList.filter(predicate)
                        _uiState.update {
                            it.copy(
                                status = Status.LOADED,
                                isRefreshing = false,
                                serviceAlerts = filteredServiceAlerts,
                                informationAlerts = filteredInformationAlerts,
                            )
                        }
                    }.catch {
                        _uiState.update {
                            it.copy(
                                status = Status.ERROR,
                                isRefreshing = false,
                            )
                        }
                    }.launchIn(viewModelScope)
                }.onFailure {
                    _uiState.update {
                        it.copy(
                            status = Status.ERROR,
                            isRefreshing = false,
                        )
                    }
                }
            }
        }
    }
}

data class AlertsUIState(
    val status: Status = Status.LOADING,
    val informationAlerts: List<Alert> = emptyList(),
    val serviceAlerts: List<Alert> = emptyList(),
    val isRefreshing: Boolean = false,
)