package com.jsontextfield.departurescreen.core.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object AlertsRoute

@Serializable
data object StationsRoute

@Serializable
data class TripRoute(val id: String? = null)