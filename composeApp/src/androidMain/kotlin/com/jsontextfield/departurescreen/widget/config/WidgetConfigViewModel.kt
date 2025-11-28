package com.jsontextfield.departurescreen.widget.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Station
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
                preferencesRepository.getSelectedStationCode(),
            ) { widgetConfig, selectedStationCode ->
                val allStations = goTrainDataSource.getAllStations()
                _config.update {
                    val stationCode = widgetConfig.selectedStationCode ?: selectedStationCode
                    widgetConfig.copy(
                        selectedStation = allStations.firstOrNull {
                            stationCode in it.code
                        },
                        selectedStationCode = stationCode,
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

    fun onStationChanged(station: Station) {
        _config.update {
            it.copy(
                selectedStation = station,
                selectedStationCode = station.code.split(",").first()
            )
        }
    }
}

data class WidgetConfig(
    val selectedStation: Station? = null,
    val selectedStationCode: String? = null,
    val sortMode: SortMode = SortMode.TIME,
    val opacity: Float = 0.8f,
)
