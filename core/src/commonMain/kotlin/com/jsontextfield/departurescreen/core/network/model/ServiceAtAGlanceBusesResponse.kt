package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ServiceAtAGlanceBusesResponse(
    @JsonNames("Metadata") val metadata: Metadata? = null,
    @JsonNames("Trips") val trips: Trips? = null,
) {
    @Serializable
    data class Trips(
        @JsonNames("Trip") val trip: List<Trip> = emptyList(),
    ) {
        @Serializable
        data class Trip(
            @JsonNames("BusType") val busType: String = "",
            @JsonNames("TripNumber") val tripNumber: String = "",
            @JsonNames("StartTime") val startTime: String? = "",
            @JsonNames("EndTime") val endTime: String? = "",
            @JsonNames("LineCode") val lineCode: String = "",
            @JsonNames("RouteNumber") val routeNumber: String? = "",
            @JsonNames("VariantDir") val variantDir: String? = "",
            @JsonNames("Display") val display: String? = "",
            @JsonNames("Latitude") val latitude: Double = 0.0,
            @JsonNames("Longitude") val longitude: Double = 0.0,
            @JsonNames("IsInMotion") val isInMotion: Boolean = false,
            @JsonNames("DelaySeconds") val delaySeconds: Int = -1,
            @JsonNames("Course") val course: Double = 0.0,
            @JsonNames("FirstStopCode") val firstStopCode: String? = "",
            @JsonNames("LastStopCode") val lastStopCode: String? = "",
            @JsonNames("PrevStopCode") val prevStopCode: String? = "",
            @JsonNames("NextStopCode") val nextStopCode: String? = "",
            @JsonNames("AtStationCode") val atStationCode: String? = "",
            @JsonNames("ModifiedDate") val modifiedDate: String? = "",
            @JsonNames("OccupancyPercentage") val occupancyPercentage: Int = -1,
        )
    }
}

