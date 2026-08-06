package com.jsontextfield.departurescreen.widget.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Stop
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WidgetConfigViewModel(
    private val preferencesRepository: IPreferencesRepository,
    private val widgetConfigDataStore: WidgetConfigDataStore,
    private val widgetId: Int? = null,
) : ViewModel() {

    private var _config: MutableStateFlow<WidgetConfig> = MutableStateFlow(WidgetConfig())
    val config: StateFlow<WidgetConfig> = _config.asStateFlow()

    init {
        widgetId?.let {
            combine(
                widgetConfigDataStore.getConfig(widgetId),
                preferencesRepository.getSelectedStop(),
            ) { widgetConfig, selectedStopCode ->
                _config.update {
                    val stopName = widgetConfig.selectedStopName ?: selectedStopCode
                    widgetConfig.copy(
                        selectedStopName = stopName,
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    fun saveConfig(widgetId: Int) {
        viewModelScope.launch {
            widgetConfigDataStore.saveConfig(widgetId, _config.value)
        }
    }

    fun onSortModeChanged(sortMode: SortMode) {
        _config.update {
            it.copy(
                sortMode = sortMode
            )
        }
    }

    fun onTimeFormatChanged(timeFormat: TimeFormat) {
        _config.update {
            it.copy(
                timeFormat = timeFormat
            )
        }
    }

    fun onOpacityChanged(opacity: Float) {
        _config.update {
            it.copy(
                opacity = opacity
            )
        }
    }

    fun onStopChanged(stop: Stop) {
        _config.update {
            it.copy(
                selectedStopName = stop.name
            )
        }
    }
}

data class WidgetConfig(
    val selectedStopName: String? = null,
    val sortMode: SortMode = SortMode.TIME,
    val timeFormat: TimeFormat = TimeFormat.RELATIVE,
    val opacity: Float = 0.8f,
)
