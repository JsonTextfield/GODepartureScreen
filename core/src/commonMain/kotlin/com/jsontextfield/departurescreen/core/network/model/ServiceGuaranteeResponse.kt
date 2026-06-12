package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceGuaranteeResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("Stops") val stops: Stops? = null,
) {
    @Serializable
    data class Stops(
        @SerialName("Stop") val stop: List<Stop>? = emptyList(),
    ) {
        @Serializable
        data class Stop(
            @SerialName("Code") val code: String = "",
            @SerialName("Scope") val scope: String? = "",
            @SerialName("ReasonEn") val reasonEn: String? = "",
            @SerialName("ReasonFr") val reasonFr: String? = "",
        ) {
            override fun toString(): String {
                return "$scope - $code: $reasonEn / $reasonFr"
            }
        }
    }
}