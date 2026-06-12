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
            @SerialName("DirectionName") val directionName: String = "",
            @SerialName("DepartureStatus") val departureStatus: String = "",
            @SerialName("TripOrder") val tripOrder: Int = -1,
            @SerialName("TripNumber") val tripNumber: String = "",
            @SerialName("Status") val status: String = "",
            @SerialName("ScheduledPlatform") val scheduledPlatform: String? = "",
            @SerialName("ActualPlatform") val actualPlatform: String? = "",
            @SerialName("ScheduledDepartureTime") val scheduledDepartureTime: String? = "",
            @SerialName("ComputedDepartureTime") val computedDepartureTime: String? = "",
            @SerialName("ServiceType") val serviceType: String? = "",
        )
    }
}