package com.jsontextfield.departurescreen.ui.navigation

data class NavigationActions(
    val onShowAlerts: () -> Unit = {},
    val onShowStations: () -> Unit = {},
)