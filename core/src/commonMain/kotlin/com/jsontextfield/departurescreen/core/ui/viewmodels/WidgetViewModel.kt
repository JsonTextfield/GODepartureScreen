package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val departureScreenUseCase: DepartureScreenUseCase,
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {
    private var _uiState = MutableStateFlow(WidgetUIState())
    val uiState = _uiState.asStateFlow()

    init {
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

        combine(
            departureScreenUseCase.getSelectedStation(),
            preferencesRepository.getFavouriteStations(),
        ) { selectedStation, favouriteStations ->
            _uiState.update {
                it.copy(
                    selectedStation = selectedStation?.copy(
                        isFavourite = selectedStation.codes.any { code -> code in favouriteStations }
                    )
                )
            }
        }.launchIn(viewModelScope)

        departureScreenUseCase.getSelectedStation()
            .distinctUntilChanged()
            .onEach { selectedStation ->
                fetchDepartureData()
            }
            .catch { _ ->
                _uiState.update { it.copy(status = Status.ERROR) }
            }.launchIn(viewModelScope)
    }

    fun fetchDepartureData() {
        val station = uiState.value.selectedStation ?: return

        viewModelScope.launch {
            runCatching {
                val stationCodes = station.codes
                stationCodes.flatMap { goTrainDataSource.getTrains(it) }
            }.onSuccess { trains ->
                val trainCodes =
                    trains.map { it.code }.toSet() intersect _uiState.value.visibleTrains
                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        _allTrips = trains,
                        visibleTrains = trainCodes,
                        _lastUpdated = Clock.System.now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                            .time
                    )
                }
            }
        }
    }
}

data class WidgetUIState(
    val status: Status = Status.LOADING,
    val selectedStation: CombinedStation? = null,
    private val _allTrips: List<Trip> = emptyList(),
    val visibleTrains: Set<String> = emptySet(),
    val sortMode: SortMode = SortMode.TIME,
    val theme: ThemeMode = ThemeMode.DEFAULT,
    val isRefreshing: Boolean = false,
    private val _lastUpdated: LocalTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).time,
) {
    val allTrips: List<Trip>
        get() {
            return _allTrips.filter { train ->
                train.code in visibleTrains || visibleTrains.isEmpty()
            }.sortedWith(
                when (sortMode) {
                    SortMode.TIME -> compareBy { it.departureTime }
                    SortMode.LINE -> compareBy({ it.code }, { it.destination })
                }
            )
        }

    val lastUpdated: String = _lastUpdated
        .format(LocalTime.Format {
            hour(padding = Padding.ZERO)
            char(':')
            minute(padding = Padding.ZERO)
        })
}