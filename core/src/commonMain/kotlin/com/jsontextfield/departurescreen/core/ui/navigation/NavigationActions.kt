package com.jsontextfield.departurescreen.core.ui.navigation

data class NavigationActions(
    val onShowAlerts: () -> Unit = {},
    val onShowStations: () -> Unit = {},
    val onShowTripDetails: (String) -> Unit = {},
)