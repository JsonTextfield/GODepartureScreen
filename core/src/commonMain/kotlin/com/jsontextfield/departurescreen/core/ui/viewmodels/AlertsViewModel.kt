@file:OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)

package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class AlertsViewModel(
    private val goTrainDataSource: ITransitRepository,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<AlertsUIState> = MutableStateFlow(AlertsUIState())
    val uiState: StateFlow<AlertsUIState> = _uiState.asStateFlow()

    init {
        combine(
            preferencesRepository.getVisibleAlertLines().distinctUntilChanged(),
            preferencesRepository.getIsUnreadAlertsSelected().distinctUntilChanged(),
        ) { visibleAlertLines, isUnreadSelected ->
            _uiState.update {
                it.copy(
                    selectedLines = visibleAlertLines,
                    isUnreadSelected = isUnreadSelected,
                )
            }
        }.launchIn(viewModelScope)
        loadData()
    }

    fun loadData(language: String = "en") {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
                isRefreshing = false,
            )
        }
        loadAlerts(language)
    }

    fun refresh(language: String = "en") {
        _uiState.update {
            it.copy(isRefreshing = true)
        }
        viewModelScope.launch {
            delay(500)
            loadAlerts(language)
        }
    }

    private fun loadAlerts(language: String = "en") {
        viewModelScope.launch {
            val alertsFlow = combine(
                goTrainDataSource.getServiceAlerts(),
                goTrainDataSource.getInformationAlerts(),
                goTrainDataSource.getMarketingAlerts(),
            ) { service, info, marketing ->
                service + info + marketing
            }
            combine(
                preferencesRepository.getReadAlerts().take(1),
                preferencesRepository.getUseAlertsWithLinks().flatMapLatest { useLinks ->
                    if (useLinks) {
                        combine(
                            alertsFlow,
                            goTrainDataSource.getServiceUpdates(language),
                        ) { alerts, serviceUpdates ->
                            alerts + serviceUpdates
                        }
                    } else {
                        alertsFlow
                    }
                },
            ) { readAlerts, alerts ->
                val allLines = alerts
                    .flatMap { it.affectedLines }
                    .distinct()
                    .sorted()
                val allAlerts = alerts
                    .distinctBy { it.id }
                    .map { it.copy(isRead = it.id in readAlerts) }
                    .sortedByDescending { it.date }
                _uiState.update { uiState ->
                    uiState.copy(
                        status = Status.LOADED,
                        isRefreshing = false,
                        allAlerts = allAlerts,
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
            }.collect()
        }
    }

    fun readAlert(id: String) {
        viewModelScope.launch {
            preferencesRepository.addReadAlert(id)
        }
    }

    fun setFilter(lines: Set<String>, isUnreadSelected: Boolean) {
        _uiState.update {
            it.copy(
                selectedLines = lines,
                isUnreadSelected = isUnreadSelected,
            )
        }
        viewModelScope.launch {
            preferencesRepository.setVisibleAlertLines(lines)
            preferencesRepository.setIsUnreadAlertsSelected(isUnreadSelected)
        }
    }

}

@Immutable
data class AlertsUIState(
    val status: Status = Status.LOADING,
    val allAlerts: List<Alert> = emptyList(),
    val allLines: List<String> = emptyList(),
    val selectedLines: Set<String> = emptySet(),
    val isUnreadSelected: Boolean = false,
    val isRefreshing: Boolean = false,
) {
    private val filterPredicate: (Alert) -> Boolean = { alert ->
        (isUnreadSelected && alert.isRead) xor (selectedLines.isEmpty() || alert.affectedLines.any { lineCode ->
            lineCode in selectedLines
        })
    }
    val alerts: List<Alert> = allAlerts.filter(filterPredicate)
}