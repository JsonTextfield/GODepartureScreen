package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NextServiceResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("NextService") val nextService: NextService? = null,
) {
    @Serializable
    data class NextService(
        @SerialName("Lines") val lines: List<Line> = emptyList(),
    ) {
        @Serializable
        data class Line(
            @SerialName("StopCode") val stopCode: String = "",
            @SerialName("LineCode") val lineCode: String = "",
            @SerialName("LineName") val lineName: String = "",
            @SerialName("ServiceType") val serviceType: String? = "",
            @SerialName("DirectionCode") val directionCode: String = "",
            @SerialName("DirectionName") val directionName: String = "",
            @SerialName("ScheduledDepartureTime") val scheduledDepartureTime: String? = null,
            @SerialName("ComputedDepartureTime") val computedDepartureTime: String? = null,
            @SerialName("DepartureStatus") val departureStatus: String = "",
            @SerialName("ScheduledPlatform") val scheduledPlatform: String? = "",
            @SerialName("ActualPlatform") val actualPlatform: String? = "",
            @SerialName("TripOrder") val tripOrder: Int = -1,
            @SerialName("TripNumber") val tripNumber: String = "",
            @SerialName("UpdateTime") val updateTime: String = "",
            @SerialName("Status") val status: String = "",
            @SerialName("Latitude") val latitude: Double? = null,
            @SerialName("Longitude") val longitude: Double? = null,
        )
    }
}