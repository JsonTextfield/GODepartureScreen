package com.jsontextfield.departurescreen.core.network

actual suspend fun isAdEnabled(): Boolean {
    return FeatureFlagApi.getFeatureFlags().firstOrNull {
        it.app == "departures" && it.name == "ads" && it.platform == "ios"
    }?.enabled == true
}