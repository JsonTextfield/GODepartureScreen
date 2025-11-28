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

    operator fun invoke(selectedStation: String? = null): Flow<Station?> {
        return preferencesRepository.getSelectedStationCode().map { prefStationCode ->
            val stationCode = selectedStation ?: prefStationCode
            val allStations = goTrainDataSource.getAllStations()
            allStations.firstOrNull { station -> stationCode in station.code }
        }
    }
}