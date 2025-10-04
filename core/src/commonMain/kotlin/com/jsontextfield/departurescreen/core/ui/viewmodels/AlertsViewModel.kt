package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlertsViewModel(
    private val departureScreenUseCase: DepartureScreenUseCase,
    private val goTrainDataSource: IGoTrainDataSource,
) : ViewModel() {
    private val _uiState: MutableStateFlow<AlertsUIState> = MutableStateFlow(AlertsUIState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAlerts()
    }

    fun refresh() {
        _uiState.update {
            it.copy(isRefreshing = true)
        }
        loadAlerts()
    }

    fun loadAlerts() {
        viewModelScope.launch {
            departureScreenUseCase.getSelectedStation().catch {
                _uiState.update { it.copy(status = Status.ERROR) }
            }.collectLatest { selectedStation ->
                runCatching {
                    val selectedStationCodes = selectedStation?.codes ?: emptySet()
                    val allTrainCodes =
                        selectedStationCodes.flatMap { goTrainDataSource.getTrains(it).map { it.code } }.toSet()

                    val predicate: (Alert) -> Boolean = {
                        (selectedStationCodes.any { code -> code in it.affectedStations } || it.affectedStations.isEmpty())
                                &&
                                (allTrainCodes.any { code -> code in it.affectedLines } || it.affectedLines.isEmpty())
                    }
                    val serviceAlerts = goTrainDataSource.getServiceAlerts()
                    val informationAlerts = goTrainDataSource.getInformationAlerts()
                    serviceAlerts.filter(predicate) to informationAlerts.filter(predicate)
                }
                    .onSuccess { (filteredInformationAlerts, filteredServiceAlerts) ->
                        _uiState.update {
                            it.copy(
                                status = Status.LOADED,
                                serviceAlerts = filteredServiceAlerts,
                                informationAlerts = filteredInformationAlerts,
                                isRefreshing = false,
                            )
                        }
                    }
                    .onFailure {
                        _uiState.update { it.copy(status = Status.ERROR) }
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