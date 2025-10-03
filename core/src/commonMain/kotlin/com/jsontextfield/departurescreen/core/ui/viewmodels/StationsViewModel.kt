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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StationsViewModel(
    private val departureScreenUseCase: DepartureScreenUseCase,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<StationsUIState> = MutableStateFlow(StationsUIState())
    val uiState: StateFlow<StationsUIState> = _uiState.asStateFlow()

    private val stationComparator = compareByDescending<CombinedStation> { it.isFavourite }
        .thenBy { "UN" !in it.codes && "02300" !in it.codes }
        .thenBy { it.name }

    init {
        _uiState.update { it.copy(status = Status.LOADING) }
        viewModelScope.launch {
            combine(
                preferencesRepository.getFavouriteStations(),
                departureScreenUseCase.getSelectedStation(),
            ) { favouriteStationCodes, selectedStation ->
                val allStations = departureScreenUseCase.getAllCombinedStations()
                // Map and sort the stations with the latest favorite information
                val updatedStations = allStations.map { station ->
                    station.copy(
                        isFavourite = station.codes.any { code -> code in favouriteStationCodes }
                    )
                }.sortedWith(stationComparator)

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
        _uiState.update { it.copy(selectedStation = station) }
        viewModelScope.launch {
            preferencesRepository.setSelectedStationCode(station.codes.first())
        }
    }

    fun setFavouriteStations(station: CombinedStation) {
        viewModelScope.launch {
            val favouriteStationCodes = preferencesRepository.getFavouriteStations().first()

            val updatedStations = if (station.codes.any { it in favouriteStationCodes }) {
                favouriteStationCodes - station.codes
            } else {
                favouriteStationCodes + station.codes
            }
            preferencesRepository.setFavouriteStations(updatedStations)

            _uiState.update {
                it.copy(
                    selectedStation = it.selectedStation?.copy(isFavourite = it.selectedStation.codes.any { it in updatedStations })
                )
            }
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