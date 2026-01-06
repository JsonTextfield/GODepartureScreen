@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class AlertsViewModel(
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
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
        viewModelScope.launch {
            delay(500)
            loadAlerts()
        }
    }

    private fun loadAlerts() {
        combine(
            preferencesRepository.getReadAlerts().take(1),
            goTrainDataSource.getServiceAlerts(),
            goTrainDataSource.getInformationAlerts(),
        ) { readAlerts, serviceAlerts, informationAlerts ->
            val allStations = goTrainDataSource.getAllStations().associate {
                it.code to it.name
            }
            val replaceGTWithKI: (String) -> String = { lineCode ->
                if (lineCode == "GT") {
                    "KI"
                } else {
                    lineCode
                }
            }
            val allLines = (serviceAlerts + informationAlerts)
                .flatMap { it.affectedLines }
                .map(replaceGTWithKI)
                .distinct()
                .sorted()
            val allServiceAlerts = serviceAlerts.map {
                it.copy(
                    isRead = it.id in readAlerts,
                    affectedLines = it.affectedLines.map(replaceGTWithKI),
                    affectedStations = it.affectedStations.map { stationCode ->
                        allStations.firstNotNullOf { (code, name) ->
                            if (stationCode in code) name else null
                        }
                    },
                )
            }
            val allInformationAlerts = informationAlerts.map {
                it.copy(
                    isRead = it.id in readAlerts,
                    affectedLines = it.affectedLines.map(replaceGTWithKI),
                    affectedStations = it.affectedStations.map { stationCode ->
                        allStations.firstNotNullOf { (code, name) ->
                            if (stationCode in code) name else null
                        }
                    },
                )
            }
            _uiState.update { uiState ->
                uiState.copy(
                    status = Status.LOADED,
                    isRefreshing = false,
                    allServiceAlerts = allServiceAlerts,
                    allInformationAlerts = allInformationAlerts,
                    selectedLines = uiState.selectedLines,
                    allLines = allLines,
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
    }

    fun readAlert(id: String) {
        Logger.withTag(AlertsViewModel::class.simpleName.toString()).d("Read alert $id")
        viewModelScope.launch {
            preferencesRepository.addReadAlert(id)
        }
    }

    fun filterLines(lines: Set<String>, isUnreadSelected: Boolean) {
        _uiState.update {
            it.copy(
                selectedLines = lines,
                isUnreadSelected = isUnreadSelected,
            )
        }
    }

}

data class AlertsUIState(
    val status: Status = Status.LOADING,
    val allInformationAlerts: List<Alert> = emptyList(),
    val allServiceAlerts: List<Alert> = emptyList(),
    val allLines: List<String> = emptyList(),
    val selectedLines: Set<String> = emptySet(),
    val isUnreadSelected: Boolean = false,
    val isRefreshing: Boolean = false,
) {
    val serviceAlerts: List<Alert> = allServiceAlerts.filter { alert ->
        (isUnreadSelected && alert.isRead) xor (selectedLines.isEmpty() || alert.affectedLines.any { lineCode ->
            lineCode in selectedLines
        })
    }
    val informationAlerts: List<Alert> = allInformationAlerts.filter { alert ->
        (isUnreadSelected && alert.isRead) xor (selectedLines.isEmpty() || alert.affectedLines.any { lineCode ->
            lineCode in selectedLines
        })
    }
}