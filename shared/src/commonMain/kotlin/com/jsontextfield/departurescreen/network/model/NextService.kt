package com.jsontextfield.departurescreen.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@ExperimentalSerializationApi
@Serializable
data class NextServiceResponse(
    @JsonNames("NextService") val nextService: NextService
) {
    @Serializable
    data class NextService(
        @JsonNames("Lines") val lines: List<Line>
    ) {
        @Serializable
        data class Line(
            @JsonNames("StopCode") val stopCode: String,
            @JsonNames("LineCode") val lineCode: String,
            @JsonNames("LineName") val lineName: String,
            @JsonNames("ServiceType") val serviceType: String,
            @JsonNames("DirectionCode") val directionCode: String,
            @JsonNames("DirectionName") val directionName: String,
            @JsonNames("ScheduledDepartureTime") val scheduledDepartureTime: String,
            @JsonNames("ComputedDepartureTime") val computedDepartureTime: String,
            @JsonNames("DepartureStatus") val departureStatus: String,
            @JsonNames("ScheduledPlatform") val scheduledPlatform: String,
            @JsonNames("ActualPlatform") val actualPlatform: String,
            @JsonNames("TripOrder") val tripOrder: Int,
            @JsonNames("TripNumber") val tripNumber: String,
            @JsonNames("UpdateTime") val updateTime: String,
            @JsonNames("Status") val status: String,
            @JsonNames("Latitude") val latitude: Double,
            @JsonNames("Longitude") val longitude: Double
        )
    }
}