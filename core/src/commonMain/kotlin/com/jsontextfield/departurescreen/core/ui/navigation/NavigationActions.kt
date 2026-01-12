package com.jsontextfield.departurescreen.core.ui.navigation

import com.jsontextfield.departurescreen.core.entities.Trip

data class NavigationActions(
    val onShowAlerts: () -> Unit = {},
    val onShowStations: () -> Unit = {},
    val onShowTripDetails: (Trip) -> Unit = {},
)