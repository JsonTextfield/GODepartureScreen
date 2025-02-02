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
            @JsonNames("DirectionName") val directionName: String,
            @JsonNames("DepartureStatus") val departureStatus: String,
            @JsonNames("TripOrder") val tripOrder: Int,
            @JsonNames("TripNumber") val tripNumber: String,
            @JsonNames("Status") val status: String,
        )
    }
}