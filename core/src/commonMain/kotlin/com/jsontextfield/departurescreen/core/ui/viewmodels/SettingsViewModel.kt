package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SettingsUIState> = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState.asStateFlow()

    init {
        combine(
            preferencesRepository.getTheme(),
            preferencesRepository.getTimeFormat(),
        ) { theme, timeFormat ->
            _uiState.update {
                it.copy(
                    themeMode = theme,
                    timeFormat = timeFormat,
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onTimeFormatChange(newTimeFormat: TimeFormat) {
        viewModelScope.launch {
            preferencesRepository.setTimeFormat(newTimeFormat)
        }
    }

    fun onThemeModeChange(newThemeMode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setTheme(newThemeMode)
        }
    }
}

data class SettingsUIState(
    val themeMode: ThemeMode = ThemeMode.DEFAULT,
    val timeFormat: TimeFormat = TimeFormat.RELATIVE,
)