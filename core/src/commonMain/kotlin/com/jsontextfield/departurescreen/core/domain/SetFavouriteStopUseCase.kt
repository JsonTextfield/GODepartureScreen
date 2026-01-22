package com.jsontextfield.departurescreen.core.domain

import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.entities.Stop
import kotlinx.coroutines.flow.first

class SetFavouriteStopUseCase(
    private val preferencesRepository: IPreferencesRepository,
) {
    suspend operator fun invoke(stop: Stop) {
        val favouriteStopCodes = preferencesRepository.getFavouriteStops().first()
        val stopCodes = stop.code.split(",").toSet()
        val updatedStops = if (stopCodes.any { it in favouriteStopCodes }) {
            favouriteStopCodes - stopCodes
        } else {
            favouriteStopCodes + stopCodes
        }
        preferencesRepository.setFavouriteStops(updatedStops)
    }
}