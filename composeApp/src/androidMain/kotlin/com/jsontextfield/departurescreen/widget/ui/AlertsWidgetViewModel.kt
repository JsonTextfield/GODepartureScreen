@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.widget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AlertsWidgetViewModel(
    private val transitRepository: ITransitRepository,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {
    private var _uiState: MutableStateFlow<AlertsWidgetUIState> = MutableStateFlow(AlertsWidgetUIState())
    val uiState: StateFlow<AlertsWidgetUIState> = _uiState.asStateFlow()

    init {
        preferencesRepository.getTimeFormat()
            .map { timeFormat -> _uiState.update { it.copy(timeFormat = timeFormat) } }
            .launchIn(viewModelScope)
        loadAlerts()
    }

    fun refresh() {
        _uiState.update {
            it.copy(
                isRefreshing = true,
                status = Status.LOADING,
            )
        }
        loadAlerts()
    }

    private fun loadAlerts(language: String = "en") {
        combine(
            transitRepository.getAllAlerts(language),
            preferencesRepository.getReadAlerts().take(1)
        ) { alerts, readAlerts ->
            val allAlerts = alerts
                .map { it.copy(isRead = it.id in readAlerts) }
                .sortedByDescending { it.date }
            _uiState.update {
                it.copy(
                    status = Status.LOADED,
                    isRefreshing = false,
                    _alerts = allAlerts,
                    _lastUpdated = Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .time
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
}

data class AlertsWidgetUIState(
    val status: Status = Status.LOADING,
    val _alerts: List<Alert> = emptyList(),
    val timeFormat: TimeFormat = TimeFormat.RELATIVE,
    val isRefreshing: Boolean = false,
    private val _lastUpdated: LocalTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).time,
) {
    val alerts: List<Alert> = _alerts

    val lastUpdated: String = _lastUpdated
        .format(LocalTime.Format {
            hour(padding = Padding.ZERO)
            char(':')
            minute(padding = Padding.ZERO)
            char(':')
            second(padding = Padding.ZERO)
        })
}
