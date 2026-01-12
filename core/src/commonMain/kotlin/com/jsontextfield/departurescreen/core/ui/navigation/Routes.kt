package com.jsontextfield.departurescreen.core.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object AlertsRoute

@Serializable
data class StationsRoute(val selectedStationCode: String? = null)

@Serializable
data class TripDetailsRoute(
    val selectedStop: String,
    val tripId: String,
    val lineCode: String,
    val destination: String,
)