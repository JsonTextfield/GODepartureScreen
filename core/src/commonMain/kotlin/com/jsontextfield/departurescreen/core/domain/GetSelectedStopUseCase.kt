package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Stop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetSelectedStopUseCase(
    private val preferencesRepository: IPreferencesRepository,
    private val goTrainDataSource: ITransitRepository
) {
    /**
     * Returns the selected stop.
     * If the [stopCode] passed in is `null` or not found, then the selected stop is determined in this order:
     *
     * 1. selected stop from the preferences
     * 2. first stop with code `UN`
     * 3. first stop in the list
     */
    operator fun invoke(stopCode: String? = null): Flow<Stop?> {
        return preferencesRepository.getSelectedStopCode().map { prefStopCode ->
            val allStops = goTrainDataSource.getAllStops()
            allStops.firstOrNull { stop -> stopCode?.let { stopCode in stop.code } == true }
                ?: allStops.firstOrNull { stop -> prefStopCode in stop.code }
                ?: allStops.firstOrNull()
        }
    }
}