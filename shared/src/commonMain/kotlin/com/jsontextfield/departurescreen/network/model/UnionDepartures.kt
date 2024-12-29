package com.jsontextfield.departurescreen.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@ExperimentalSerializationApi
@Serializable
data class UnionDeparturesResponse(
    @JsonNames("AllDepartures") val unionDepartures: UnionDepartures
) {
    @Serializable
    data class UnionDepartures(
        @JsonNames("Trip") val trips: List<Trip>
    ) {
        @Serializable
        data class Trip(
            @JsonNames("Info") val info: String,
            @JsonNames("TripNumber") val tripNumber: String,
            @JsonNames("Platform") val platform: String,
            @JsonNames("Service") val service: String,
            @JsonNames("ServiceType") val serviceType: String,
            @JsonNames("Time") val time: String
        )
    }
}