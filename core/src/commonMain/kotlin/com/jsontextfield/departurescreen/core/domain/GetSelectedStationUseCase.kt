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
    private var selectedStationCode: String? = null

    operator fun invoke(selectedStationCode: String? = null): Flow<Station?> {
        this.selectedStationCode = selectedStationCode ?: this.selectedStationCode
        return preferencesRepository.getSelectedStationCode().map { selectedStationCode ->
            val allStations = goTrainDataSource.getAllStations()
            allStations.firstOrNull { station -> this.selectedStationCode?.let { it in station.code } == true }
                ?: allStations.firstOrNull { station -> selectedStationCode in station.code }
                ?: allStations.firstOrNull()
        }
    }
}