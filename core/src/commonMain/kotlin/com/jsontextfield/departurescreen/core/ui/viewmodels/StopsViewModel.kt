package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStopUseCase
import com.jsontextfield.departurescreen.core.domain.SetFavouriteStopUseCase
import com.jsontextfield.departurescreen.core.entities.Stop
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.StopType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StopsViewModel(
    private val getSelectedStopUseCase: GetSelectedStopUseCase,
    private val setFavouriteStopUseCase: SetFavouriteStopUseCase,
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
    selectedStopCode: String? = null,
) : ViewModel() {
    private val _uiState: MutableStateFlow<StopsUIState> = MutableStateFlow(StopsUIState())
    val uiState: StateFlow<StopsUIState> = _uiState.asStateFlow()

    init {
        loadData(selectedStopCode)
    }

    fun loadData(selectedStopCode: String? = null) {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
            )
        }
        combine(
            getSelectedStopUseCase(selectedStopCode),
            preferencesRepository.getFavouriteStops(),
        ) { selectedStop, favouriteStopCodes ->
            val allStops = goTrainDataSource.getAllStops()
            val updatedStops = allStops.map { stop ->
                stop.copy(
                    isFavourite = stop.code.split(",").any { code -> code in favouriteStopCodes }
                )
            }.sortedWith(
                compareByDescending<Stop> { it.isFavourite }
                    .thenByDescending { "UN" in it.code || "02300" in it.code }
                    .thenBy { it.name }
            )

            _uiState.update {
                it.copy(
                    status = Status.LOADED,
                    allStops = updatedStops,
                    selectedStop = selectedStop,
                )
            }
        }.catch {
            _uiState.update { it.copy(status = Status.ERROR) }
        }.launchIn(viewModelScope)
    }

    fun setSelectedStop(stop: Stop) {
        viewModelScope.launch {
            preferencesRepository.setSelectedStopCode(stop.code.split(",").first())
        }
    }

    fun setFavouriteStops(stop: Stop) {
        viewModelScope.launch {
            setFavouriteStopUseCase(stop)
        }
    }

    fun setStopType(stopType: StopType?) {
        _uiState.update {
            it.copy(
                stopType = stopType,
            )
        }
    }

}

data class StopsUIState(
    val status: Status = Status.LOADING,
    val allStops: List<Stop> = emptyList(),
    val selectedStop: Stop? = null,
    val stopType: StopType? = null,
) {
    fun getFilteredStops(query: String): List<Stop> {
        if (stopType == null && query.isBlank()) return allStops
        val lowerCaseQuery = query.lowercase()
        return allStops.filter {
            (stopType == null || stopType in it.types) && lowerCaseQuery in it.name.lowercase()
        }
    }
}