package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlagsResponse(
    val id: String,
    val name: String? = null,
    val minBuildNumber: Int = 1,
    val enabled: Boolean = false,
    val platform: String? = null,
    val app: String? = null,
)