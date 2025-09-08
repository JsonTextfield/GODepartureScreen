package com.jsontextfield.departurescreen.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@ExperimentalSerializationApi
@Serializable
data class UnionDeparturesResponse(
    @JsonNames("Metadata") val metadata: Metadata? = null,
    @JsonNames("AllDepartures") val allDepartures: AllDepartures? = null,
) {
    @Serializable
    data class AllDepartures(
        @JsonNames("Trip") val trips: List<Trip> = emptyList(),
    ) {
        @Serializable
        data class Trip(
            @JsonNames("Info") val info: String = "",
            @JsonNames("TripNumber") val tripNumber: String = "",
            @JsonNames("Platform") val platform: String = "",
            @JsonNames("Service") val service: String = "",
            @JsonNames("Time") val time: String = "",
            @JsonNames("Stops") val stops: List<Stop> = emptyList(),
        ) {
            @Serializable
            data class Stop(
                @JsonNames("Name") val name: String = "",
                @JsonNames("Code") val code: String? = null,
            )
        }
    }
}