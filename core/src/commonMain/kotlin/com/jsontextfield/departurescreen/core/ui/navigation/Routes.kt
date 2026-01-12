package com.jsontextfield.departurescreen.core.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object AlertsRoute

@Serializable
data class StopsRoute(val selectedStopCode: String? = null)

@Serializable
data class TripDetailsRoute(
    val selectedStop: String,
    val tripId: String,
    val lineCode: String,
    val destination: String,
)