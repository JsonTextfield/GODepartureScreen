package com.jsontextfield.departurescreen.core.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.entities.Station
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
    private val goTrainDataSource: IGoTrainDataSource,
    private val preferencesRepository: IPreferencesRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<StationsUIState> = MutableStateFlow(StationsUIState())
    val uiState: StateFlow<StationsUIState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _uiState.update {
            it.copy(
                status = Status.LOADING,
            )
        }
        viewModelScope.launch {
            combine(
                departureScreenUseCase.getSelectedStation(),
                preferencesRepository.getFavouriteStations(),
            ) { selectedStation, favouriteStationCodes ->
                val allStations = goTrainDataSource.getAllStations()
                val updatedStations = allStations.map { station ->
                    station.copy(
                        isFavourite = station.code.split(",").any { code -> code in favouriteStationCodes }
                    )
                }.sortedWith(
                    compareByDescending<Station> { it.isFavourite }
                        .thenByDescending { "UN" in it.code || "02300" in it.code }
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

    fun setSelectedStation(station: Station) {
        viewModelScope.launch {
            preferencesRepository.setSelectedStationCode(station.code.split(",").first())
        }
    }

    fun setFavouriteStations(station: Station) {
        viewModelScope.launch {
            departureScreenUseCase.setFavouriteStations(station)
        }
    }

}

data class StationsUIState(
    val status: Status = Status.LOADING,
    val allStations: List<Station> = emptyList(),
    val selectedStation: Station? = null,
) {
    fun getFilteredStations(query: String): List<Station> {
        if (query.isBlank()) return allStations
        val lowerCaseQuery = query.lowercase()
        return allStations.filter {
            lowerCaseQuery in it.name.lowercase()
        }
    }
}