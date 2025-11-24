package com.jsontextfield.departurescreen.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStationUseCase
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.widget.config.WidgetConfigDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

class WidgetViewModel(
    private val getSelectedStationUseCase: GetSelectedStationUseCase,
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
    private val configDataStore: WidgetConfigDataStore,
) : ViewModel() {
    private var _uiState: MutableStateFlow<WidgetUIState> = MutableStateFlow(WidgetUIState())
    val uiState: StateFlow<WidgetUIState> = _uiState.asStateFlow()

    fun loadConfig(widgetId: Int) {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
                isRefreshing = false,
            )
        }
        combine(
            preferencesRepository.getVisibleTrains(),
            preferencesRepository.getSortMode(),
            preferencesRepository.getTheme(),
        ) { visibleTrains, sortMode, theme ->
            _uiState.update {
                it.copy(
                    visibleTrains = visibleTrains,
                    sortMode = sortMode,
                    theme = theme,
                )
            }
        }.launchIn(viewModelScope)
        viewModelScope.launch {
            configDataStore.getConfig(widgetId).collectLatest { widgetConfig ->
                getSelectedStationUseCase(widgetConfig.selectedStationCode)
                    .distinctUntilChanged()
                    .catch { _ ->
                        _uiState.update {
                            it.copy(
                                status = Status.ERROR,
                                isRefreshing = false,
                            )
                        }
                    }.collect { selectedStation ->
                        _uiState.update {
                            it.copy(
                                status = Status.LOADING,
                                isRefreshing = false,
                                selectedStation = selectedStation,
                            )
                        }
                        fetchDepartureData()
                    }
            }
        }
    }

    fun refresh() {
        _uiState.update {
            it.copy(
                isRefreshing = true,
                status = Status.LOADING,
            )
        }
        fetchDepartureData()
    }

    private fun fetchDepartureData() {
        viewModelScope.launch {
            runCatching {
                uiState.value.selectedStation?.code?.split(",")?.flatMap { goTrainDataSource.getTrips(it) }
                    ?: emptyList()
            }.onSuccess { trains ->
                val trainCodes = trains.map { it.code }.toSet() intersect _uiState.value.visibleTrains
                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        isRefreshing = false,
                        _allTrips = trains,
                        visibleTrains = trainCodes,
                        _lastUpdated = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .time
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        status = Status.ERROR,
                        isRefreshing = false,
                    )
                }
            }
        }
    }
}

data class WidgetUIState(
    val status: Status = Status.LOADING,
    val selectedStation: Station? = null,
    private val _allTrips: List<Trip> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
    val theme: ThemeMode = ThemeMode.DEFAULT,
    val isRefreshing: Boolean = false,
    private val _lastUpdated: LocalTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).time,
) {
    val allTrips: List<Trip> = _allTrips.filter { train ->
        train.code in visibleTrains || visibleTrains.isEmpty()
    }.sortedWith(
        when (sortMode) {
            SortMode.TIME -> compareBy { it.departureTime }
            SortMode.LINE -> compareBy({ it.code }, { it.destination })
        }
    )

    val lastUpdated: String = _lastUpdated
        .format(LocalTime.Format {
            hour(padding = Padding.ZERO)
            char(':')
            minute(padding = Padding.ZERO)
            char(':')
            second(padding = Padding.ZERO)
        })
}