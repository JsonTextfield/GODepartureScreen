@file:OptIn(ExperimentalSerializationApi::class)

package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ServiceGuaranteeResponse(
    @JsonNames("Metadata") val metadata: Metadata? = null,
    @JsonNames("Stops") val stops: Stops? = null,
) {
    @Serializable
    data class Stops(
        @JsonNames("Stop") val stop: List<Stop>? = emptyList(),
    ) {
        @Serializable
        data class Stop(
            @JsonNames("Code") val code: String = "",
            @JsonNames("Scope") val scope: String? = "",
            @JsonNames("ReasonEn") val reasonEn: String? = "",
            @JsonNames("ReasonFr") val reasonFr: String? = "",
        ) {
            override fun toString(): String {
                return "$scope - $code: $reasonEn / $reasonFr"
            }
        }
    }
}