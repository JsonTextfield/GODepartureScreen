package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StopResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("Stations") val stations: Stations? = null,
) {
    @Serializable
    data class Stations(
        @SerialName("Station") val stops: List<Stop>? = null,
    ) {
        @Serializable
        data class Stop(
            @SerialName("LocationCode") val locationCode: String,
            @SerialName("PublicStopId") val publicStopId: String,
            @SerialName("LocationName") val locationName: String,
            @SerialName("LocationType") val locationType: String,
        )
    }
}