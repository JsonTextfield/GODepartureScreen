package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceAtAGlanceBusesResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("Trips") val trips: Trips? = null,
) {
    @Serializable
    data class Trips(
        @SerialName("Trip") val trip: List<Trip> = emptyList(),
    ) {
        @Serializable
        data class Trip(
            @SerialName("BusType") val busType: String = "",
            @SerialName("TripNumber") val tripNumber: String = "",
            @SerialName("StartTime") val startTime: String? = "",
            @SerialName("EndTime") val endTime: String? = "",
            @SerialName("LineCode") val lineCode: String = "",
            @SerialName("RouteNumber") val routeNumber: String? = "",
            @SerialName("VariantDir") val variantDir: String? = "",
            @SerialName("Display") val display: String? = "",
            @SerialName("Latitude") val latitude: Double = 0.0,
            @SerialName("Longitude") val longitude: Double = 0.0,
            @SerialName("IsInMotion") val isInMotion: Boolean = false,
            @SerialName("DelaySeconds") val delaySeconds: Int = -1,
            @SerialName("Course") val course: Double = 0.0,
            @SerialName("FirstStopCode") val firstStopCode: String? = "",
            @SerialName("LastStopCode") val lastStopCode: String? = "",
            @SerialName("PrevStopCode") val prevStopCode: String? = "",
            @SerialName("NextStopCode") val nextStopCode: String? = "",
            @SerialName("AtStationCode") val atStationCode: String? = "",
            @SerialName("ModifiedDate") val modifiedDate: String? = "",
            @SerialName("OccupancyPercentage") val occupancyPercentage: Int = -1,
        )
    }
}

