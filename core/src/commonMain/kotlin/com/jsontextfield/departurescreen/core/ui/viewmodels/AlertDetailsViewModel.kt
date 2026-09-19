package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.network.FeatureFlagApi
import com.jsontextfield.departurescreen.core.network.isAdEnabled
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlertDetailsViewModel(
    private val transitRepository: ITransitRepository,
    private val preferencesRepository: IPreferencesRepository,
    private val featureFlagApi: FeatureFlagApi,
    private val alertId: String,
) : ViewModel() {

    private val _uiState: MutableStateFlow<AlertDetailsUIState> = MutableStateFlow(AlertDetailsUIState())
    val uiState: StateFlow<AlertDetailsUIState> = _uiState.asStateFlow()

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
    }

    fun loadAlert(language: String = "en") {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
            )
        }
        transitRepository.getAllAlerts(language).map { alerts ->
            try {
                val alert = alerts.find { it.id == alertId }
                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        alert = alert ?: throw Exception("Alert not found")
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        status = Status.ERROR
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}

data class AlertDetailsUIState(
    val status: Status = Status.LOADING,
    val timeFormat: TimeFormat = TimeFormat.RELATIVE,
    val alert: Alert? = null,
    val isAdEnabled: Boolean = false,
)