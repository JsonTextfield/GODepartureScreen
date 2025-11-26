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

    operator fun invoke(selectedStationCode: String? = null): Flow<Station?> {
        return preferencesRepository.getSelectedStationCode().map { code ->
            val allStations = goTrainDataSource.getAllStations()
            allStations.firstOrNull { station -> selectedStationCode?.let { it in station.code } == true }
                ?: allStations.firstOrNull { station -> code in station.code }
                ?: allStations.firstOrNull()
        }
    }
}