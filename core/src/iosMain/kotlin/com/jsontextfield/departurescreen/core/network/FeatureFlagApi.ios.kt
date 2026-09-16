package com.jsontextfield.departurescreen.core.network

actual suspend fun isAdEnabled(featureFlagApi: FeatureFlagApi): Boolean {
    return featureFlagApi.getFeatureFlags().firstOrNull {
        it.app == "departures" && it.name == "ads" && it.platform == "ios"
    }?.enabled == true
}