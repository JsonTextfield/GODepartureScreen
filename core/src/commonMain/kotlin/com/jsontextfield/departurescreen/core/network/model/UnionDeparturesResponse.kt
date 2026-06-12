package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnionDeparturesResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("AllDepartures") val allDepartures: AllDepartures? = null,
) {
    @Serializable
    data class AllDepartures(
        @SerialName("Trip") val trips: List<Trip> = emptyList(),
    ) {
        @Serializable
        data class Trip(
            @SerialName("Info") val info: String = "",
            @SerialName("TripNumber") val tripNumber: String = "",
            @SerialName("Platform") val platform: String = "",
            @SerialName("Service") val service: String = "",
            @SerialName("Time") val time: String = "",
            @SerialName("Stops") val stops: List<Stop> = emptyList(),
        ) {
            @Serializable
            data class Stop(
                @SerialName("Name") val name: String = "",
                @SerialName("Code") val code: String? = null,
            )
        }
    }
}