package com.jsontextfield.departurescreen.widget.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Stop
import com.jsontextfield.departurescreen.core.ui.SortMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WidgetConfigViewModel(
    private val goTrainDataSource: IGoTrainDataSource,
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
                preferencesRepository.getSelectedStopCode(),
            ) { widgetConfig, selectedStopCode ->
                val allStops = goTrainDataSource.getAllStops()
                _config.update {
                    val stopCode = widgetConfig.selectedStopCode ?: selectedStopCode
                    widgetConfig.copy(
                        selectedStop = allStops.firstOrNull {
                            stopCode in it.code
                        },
                        selectedStopCode = stopCode,
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
                selectedStop = stop,
                selectedStopCode = stop.code.split(",").first()
            )
        }
    }
}

data class WidgetConfig(
    val selectedStop: Stop? = null,
    val selectedStopCode: String? = null,
    val sortMode: SortMode = SortMode.TIME,
    val opacity: Float = 0.8f,
)
