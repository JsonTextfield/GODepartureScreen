package com.jsontextfield.departurescreen.core.ui.navigation

import com.jsontextfield.departurescreen.core.entities.Trip

data class NavigationActions(
    val onShowAlerts: () -> Unit = {},
    val onShowStops: () -> Unit = {},
    val onShowTripDetails: (Trip) -> Unit = {},
)