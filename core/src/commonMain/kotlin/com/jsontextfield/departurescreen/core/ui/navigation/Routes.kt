package com.jsontextfield.departurescreen.core.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class HomeRoute(val selectedStop: String? = null)

@Serializable
data object AlertsRoute

@Serializable
data class StopsRoute(val selectedStopName: String? = null)

@Serializable
data class TripDetailsRoute(
    val stopName: String = "",
    val stopCode: String = "",
    val tripId: String = "",
    val lineCode: String = "",
    val destination: String = "",
)

@Serializable
data object SettingsRoute