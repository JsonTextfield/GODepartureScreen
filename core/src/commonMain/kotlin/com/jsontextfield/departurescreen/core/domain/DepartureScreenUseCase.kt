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

    fun getSelectedStation(): Flow<Station?> {
        return preferencesRepository.getSelectedStationCode().map { selectedStation ->
            val allStations = goTrainDataSource.getAllStations()
             allStations.firstOrNull {
                selectedStation in it.code
            } ?: allStations.firstOrNull {
                "UN" in it.code
            } ?: allStations.firstOrNull()
        }
    }
}