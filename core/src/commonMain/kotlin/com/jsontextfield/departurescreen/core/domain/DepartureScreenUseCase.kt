package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Station
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DepartureScreenUseCase(
    private val preferencesRepository: IPreferencesRepository,
    private val goTrainDataSource: IGoTrainDataSource
) {

    private var selectedStationCode: String? = null
    suspend fun setFavouriteStations(station: Station) {
        val favouriteStationCodes = preferencesRepository.getFavouriteStations().first()
        val stationCodes = station.code.split(",")
        val updatedStations = if (stationCodes.any { it in favouriteStationCodes }) {
            favouriteStationCodes - stationCodes
        } else {
            favouriteStationCodes + stationCodes
        }
        preferencesRepository.setFavouriteStations(updatedStations)
    }

    suspend fun getFavouriteStations(): List<Station> {
        val favouriteStationCodes = preferencesRepository.getFavouriteStations().first()
        val allStations = goTrainDataSource.getAllStations()
        return allStations.filter { station ->
            favouriteStationCodes.any { it in station.code } || "UN" in station.code
        }
    }

    fun getSelectedStation(selectedStationCode: String? = null): Flow<Station?> {
        this.selectedStationCode = selectedStationCode ?: this.selectedStationCode
        return preferencesRepository.getSelectedStationCode().map { selectedStation ->
            val allStations = goTrainDataSource.getAllStations()
            allStations.firstOrNull { station -> this.selectedStationCode?.let { it in station.code } == true }
                ?: allStations.firstOrNull { selectedStation in it.code }
                ?: allStations.firstOrNull { "UN" in it.code }
                ?: allStations.firstOrNull()
        }
    }
}