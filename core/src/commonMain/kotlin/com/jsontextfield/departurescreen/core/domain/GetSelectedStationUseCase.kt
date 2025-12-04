package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Station
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSelectedStationUseCase(
    private val preferencesRepository: IPreferencesRepository,
    private val goTrainDataSource: IGoTrainDataSource
) {
    /**
     * Returns the selected station.
     * If the [stationCode] passed in is `null` or not found, then the selected station is determined in this order:
     *
     * 1. selected station from the preferences
     * 2. first station with code `UN`
     * 3. first station in the list
     */
    operator fun invoke(stationCode: String? = null): Flow<Station?> {
        return preferencesRepository.getSelectedStationCode().map { prefStationCode ->
            val allStations = goTrainDataSource.getAllStations()
            allStations.firstOrNull { station -> stationCode?.let { stationCode in station.code } == true }
                ?: allStations.firstOrNull { station -> prefStationCode in station.code }
                ?: allStations.firstOrNull()
        }
    }
}