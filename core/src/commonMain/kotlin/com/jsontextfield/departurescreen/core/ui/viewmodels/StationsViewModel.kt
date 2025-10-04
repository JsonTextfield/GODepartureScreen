package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.ui.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StationsViewModel(
    private val departureScreenUseCase: DepartureScreenUseCase,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<StationsUIState> = MutableStateFlow(StationsUIState())
    val uiState: StateFlow<StationsUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                departureScreenUseCase.getSelectedStation(),
                preferencesRepository.getFavouriteStations(),
            ) { selectedStation, favouriteStationCodes ->
                val allStations = departureScreenUseCase.getAllCombinedStations()
                val updatedStations = allStations.map { station ->
                    station.copy(
                        isFavourite = station.codes.any { code -> code in favouriteStationCodes }
                    )
                }.sortedWith(
                    compareByDescending<CombinedStation> { it.isFavourite }
                        .thenByDescending { "UN" in it.codes || "02300" in it.codes }
                        .thenBy { it.name }
                )

                _uiState.update {
                    it.copy(
                        status = Status.LOADED,
                        allStations = updatedStations,
                        selectedStation = selectedStation,
                    )
                }
            }.catch {
                _uiState.update { it.copy(status = Status.ERROR) }
            }.collect()
        }
    }

    fun setSelectedStation(station: CombinedStation) {
        viewModelScope.launch {
            preferencesRepository.setSelectedStationCode(station.codes.first())
        }
    }

    fun setFavouriteStations(station: CombinedStation) {
        viewModelScope.launch {
            departureScreenUseCase.setFavouriteStations(station)
        }
    }

}

data class StationsUIState(
    val status: Status = Status.LOADING,
    val allStations: List<CombinedStation> = emptyList(),
    val selectedStation: CombinedStation? = null,
) {
    fun getFilteredStations(query: String): List<CombinedStation> {
        if (query.isBlank()) return allStations
        val lowerCaseQuery = query.lowercase()
        return allStations.filter {
            lowerCaseQuery in it.name.lowercase()
        }
    }
}