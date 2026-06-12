package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    @SerialName("TimeStamp") val timestamp: String? = null,
    @SerialName("ErrorCode") val errorCode: String? = null,
    @SerialName("ErrorMessage") val errorMessage: String? = null,
)