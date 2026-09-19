@file:OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)

package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.network.FeatureFlagApi
import com.jsontextfield.departurescreen.core.network.isAdEnabled
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class AlertsViewModel(
    private val transitRepository: ITransitRepository,
    private val preferencesRepository: IPreferencesRepository,
    private val featureFlagApi: FeatureFlagApi,
) : ViewModel() {
    private val _uiState: MutableStateFlow<AlertsUIState> = MutableStateFlow(AlertsUIState())
    val uiState: StateFlow<AlertsUIState> = _uiState.asStateFlow()

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
        preferencesRepository.getTimeFormat().map { timeFormat ->
            _uiState.update {
                it.copy(timeFormat = timeFormat)
            }
        }.launchIn(viewModelScope)
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
        combine(
            transitRepository.getAllAlerts(language),
            preferencesRepository.getReadAlerts().take(1)
        ) { alerts, readAlerts ->
            val allLines = alerts
                .flatMap { it.affectedLines }
                .distinct()
                .sorted()
            val allAlerts = alerts
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
        }.launchIn(viewModelScope)
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
    val isAdEnabled: Boolean = false,
    val timeFormat: TimeFormat = TimeFormat.RELATIVE,
) {
    private val filterPredicate: (Alert) -> Boolean = { alert ->
        (isUnreadSelected && alert.isRead) xor (selectedLines.isEmpty() || alert.affectedLines.any { lineCode ->
            lineCode in selectedLines
        })
    }
    val alerts: List<Alert> = allAlerts.filter(filterPredicate)
}