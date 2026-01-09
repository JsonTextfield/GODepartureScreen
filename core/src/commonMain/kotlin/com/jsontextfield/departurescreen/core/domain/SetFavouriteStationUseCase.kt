package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Station
import kotlinx.coroutines.flow.first

class SetFavouriteStationUseCase(
    private val preferencesRepository: IPreferencesRepository,
) {
    suspend operator fun invoke(station: Station) {
        val favouriteStationCodes = preferencesRepository.getFavouriteStations().first()
        val stationCodes = station.code.split(",").toSet()
        val updatedStations = if (stationCodes.any { it in favouriteStationCodes }) {
            favouriteStationCodes - stationCodes
        } else {
            favouriteStationCodes + stationCodes
        }
        preferencesRepository.setFavouriteStations(updatedStations)
    }
}