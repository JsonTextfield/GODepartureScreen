package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.entities.toCombinedStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DepartureScreenUseCase(
    private val preferencesRepository: IPreferencesRepository,
    private val goTrainDataSource: IGoTrainDataSource
) {
    suspend fun getAllCombinedStations(): List<CombinedStation> {
        return goTrainDataSource.getAllStations()
            .groupBy { it.name }
            .map { it.value.toCombinedStation() }
    }

    suspend fun setFavouriteStations(station: CombinedStation) {
        val favouriteStationCodes = preferencesRepository.getFavouriteStations().first()

        val updatedStations = if (station.codes.any { it in favouriteStationCodes }) {
            favouriteStationCodes - station.codes
        } else {
            favouriteStationCodes + station.codes
        }
        preferencesRepository.setFavouriteStations(updatedStations)
    }

    fun getSelectedStation(): Flow<CombinedStation?> {
        return preferencesRepository.getSelectedStationCode().map { selectedStation ->
            val allStations = getAllCombinedStations()
             allStations.firstOrNull {
                selectedStation in it.codes
            } ?: allStations.firstOrNull {
                "UN" in it.codes
            } ?: allStations.firstOrNull()
        }
    }
}