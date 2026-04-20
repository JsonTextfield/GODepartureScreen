package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("Trips") val trips: List<Trip> = emptyList(),
) {
    @Serializable
    data class Trip(
        @SerialName("TripNumber") val tripNumber: String = "",
        @SerialName("Destination") val destination: String? = "",
        @SerialName("Status") val status: String = "",
        @SerialName("TimeStamp") val time: String = "",
        @SerialName("Stops") val stops: List<Stop> = emptyList(),
    ) {
        @Serializable
        data class Stop(
            @SerialName("Code") val code: String = "",
            @SerialName("Status") val status: String = "",
            @SerialName("Remark") val remark: String? = null,
            @SerialName("Track") val track: Track? = null,
            @SerialName("ArrivalTime") val arrivalTime: Schedule? = null,
            @SerialName("DepartureTime") val departureTime: Schedule? = null,
        ) {
            @Serializable
            data class Track(
                @SerialName("Scheduled") val scheduled: String? = null,
                @SerialName("Actual") val actual: String? = null,
            )

            @Serializable
            data class Schedule(
                @SerialName("Scheduled") val scheduled: String? = null,
                @SerialName("Computed") val computed: String? = null,
                @SerialName("Status") val status: String? = null,
            )
        }
    }
}
