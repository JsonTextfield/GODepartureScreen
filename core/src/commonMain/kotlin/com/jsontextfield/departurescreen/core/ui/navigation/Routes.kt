package com.jsontextfield.departurescreen.core.ui.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data class HomeRoute(val selectedStop: String? = null) : NavKey

@Serializable
data object AlertsRoute : NavKey

@Serializable
data class AlertDetailsRoute(val alertId: String) : NavKey

@Serializable
data class StopsRoute(val selectedStopName: String? = null) : NavKey

@Serializable
data class TripDetailsRoute(
    val stopName: String = "",
    val stopCode: String = "",
    val tripId: String = "",
    val lineCode: String = "",
    val destination: String = "",
) : NavKey

@Serializable
data object SettingsRoute : NavKey

val coreNavConfig = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(HomeRoute::class, HomeRoute.serializer())
            subclass(AlertsRoute::class, AlertsRoute.serializer())
            subclass(AlertDetailsRoute::class, AlertDetailsRoute.serializer())
            subclass(StopsRoute::class, StopsRoute.serializer())
            subclass(TripDetailsRoute::class, TripDetailsRoute.serializer())
            subclass(SettingsRoute::class, SettingsRoute.serializer())
        }
    }
}