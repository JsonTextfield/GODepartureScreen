package com.jsontextfield.departurescreen.core.network

actual suspend fun isAdEnabled(featureFlagApi: FeatureFlagApi): Boolean {
    return false
}