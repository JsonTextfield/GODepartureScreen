@file:OptIn(ExperimentalSerializationApi::class)

package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class TripResponse(
    @JsonNames("Metadata") val metadata: Metadata? = null,
    @JsonNames("Trips") val trips: List<Trip> = emptyList(),
) {
    @Serializable
    data class Trip(
        @JsonNames("TripNumber") val tripNumber: String = "",
        @JsonNames("Destination") val destination: String? = "",
        @JsonNames("Status") val status: String = "",
        @JsonNames("TimeStamp") val time: String = "",
        @JsonNames("Stops") val stops: List<Stop> = emptyList(),
    ) {
        @Serializable
        data class Stop(
            @JsonNames("Code") val code: String = "",
            @JsonNames("Status") val status: String = "",
            @JsonNames("Remark") val remark: String? = null,
        )
    }
}
