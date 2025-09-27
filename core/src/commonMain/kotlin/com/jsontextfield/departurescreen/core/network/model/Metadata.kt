package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Metadata(
    @JsonNames("TimeStamp") val timestamp: String? = null,
    @JsonNames("ErrorCode") val errorCode: String? = null,
    @JsonNames("ErrorMessage") val errorMessage: String? = null,
)