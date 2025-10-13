package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@ExperimentalSerializationApi
@Serializable
data class NextServiceResponse(
    @JsonNames("Metadata") val metadata: Metadata? = null,
    @JsonNames("NextService") val nextService: NextService? = null,
) {
    @Serializable
    data class NextService(
        @JsonNames("Lines") val lines: List<Line> = emptyList(),
    ) {
        @Serializable
        data class Line(
            @JsonNames("StopCode") val stopCode: String = "",
            @JsonNames("LineCode") val lineCode: String = "",
            @JsonNames("LineName") val lineName: String = "",
            @JsonNames("DirectionName") val directionName: String = "",
            @JsonNames("DepartureStatus") val departureStatus: String = "",
            @JsonNames("TripOrder") val tripOrder: Int = -1,
            @JsonNames("TripNumber") val tripNumber: String = "",
            @JsonNames("Status") val status: String = "",
            @JsonNames("ScheduledPlatform") val scheduledPlatform: String? = "",
            @JsonNames("ActualPlatform") val actualPlatform: String? = "",
            @JsonNames("ScheduledDepartureTime") val scheduledDepartureTime: String? = "",
            @JsonNames("ComputedDepartureTime") val computedDepartureTime: String? = "",
            @JsonNames("ServiceType") val serviceType: String? = "",
        )
    }
}