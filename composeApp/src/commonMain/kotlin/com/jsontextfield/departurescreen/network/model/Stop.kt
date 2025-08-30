package com.jsontextfield.departurescreen.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class StopResponse(
    @JsonNames("Metadata") val metadata: Metadata? = null,
    @JsonNames("Stations") val stations: Stations? = null,
) {
    @Serializable
    data class Stations(
        @JsonNames("Station") val stops: List<Stop>? = null,
    ) {
        @Serializable
        data class Stop(
            @JsonNames("LocationCode") val locationCode: String,
            @JsonNames("PublicStopId") val publicStopId: String,
            @JsonNames("LocationName") val locationName: String,
            @JsonNames("LocationType") val locationType: String,
        )
    }
}