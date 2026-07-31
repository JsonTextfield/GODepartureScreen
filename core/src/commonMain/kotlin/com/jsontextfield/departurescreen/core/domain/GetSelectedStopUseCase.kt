package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.entities.Stop
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetSelectedStopUseCase(
    private val preferencesRepository: IPreferencesRepository,
    private val goTrainDataSource: ITransitRepository
) {
    /**
     * Returns the selected stop.
     * If the [stopName] passed in is `null` or not found, then the selected stop is determined in this order:
     *
     * 1. selected stop from the preferences
     * 2. first stop in the list
     */
    operator fun invoke(stopName: String? = null): Flow<Stop?> {
        val allStopsFlow = flow {
            emit(
                goTrainDataSource.getAllStops()
                    .groupBy { it.name }
                    .map { (name, stops) ->
                        Stop(
                            name = name,
                            code = stops.joinToString(",") { it.code },
                            types = stops.flatMap { it.types }.toSet(),
                        )
                    })
        }
        return combine(
            allStopsFlow,
            preferencesRepository.getSelectedStop(),
            preferencesRepository.getFavouriteStops(),
        ) { allStops, prefStopName, favouriteStops ->
            val selectedStop = allStops.firstOrNull { it.name == stopName }
                ?: allStops.firstOrNull { it.name == prefStopName }
                ?: allStops.firstOrNull { it.code == "UN" }
                ?: allStops.firstOrNull()

            selectedStop?.copy(isFavourite = selectedStop.name in favouriteStops)
        }
    }
}