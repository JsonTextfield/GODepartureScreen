package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JourneyResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("SchJourneys") val schJourneys: List<SchJourney> = emptyList(),
) {
    @Serializable
    data class SchJourney(
        @SerialName("Date") val date: String = "",
        @SerialName("Time") val time: String = "",
        @SerialName("To") val to: String = "",
        @SerialName("From") val from: String = "",
        @SerialName("Services") val services: List<Service> = emptyList(),
    ) {
        @Serializable
        data class Service(
            @SerialName("Colour") val colour: String = "",
            @SerialName("Type") val type: String = "",
            @SerialName("Direction") val direction: String = "",
            @SerialName("Code") val code: String = "",
            @SerialName("StartTime") val startTime: String = "",
            @SerialName("EndTime") val endTime: String = "",
            @SerialName("Duration") val duration: String = "",
            @SerialName("Accessible") val accessible: String = "",
            @SerialName("Trips") val trips: Trips? = null,
        ) {
            @Serializable
            data class Trips(
                @SerialName("Trip") val trip: List<Trip> = emptyList(),
            ) {
                @Serializable
                data class Trip(
                    @SerialName("Number") val number: String = "",
                    @SerialName("Display") val display: String = "",
                    @SerialName("Line") val line: String = "",
                    @SerialName("Direction") val direction: String = "",
                    @SerialName("LineVariant") val lineVariant: String = "",
                    @SerialName("Type") val type: String = "",
                    @SerialName("Stops") val stops: Stops? = null,
                    @SerialName("destinationStopCode") val destinationStopCode: String = "",
                    @SerialName("departFromCode") val departFromCode: String = "",
                    @SerialName("departFromAlternativeCode") val departFromAlternativeCode: String? = null,
                    @SerialName("departFromTimingPoint") val departFromTimingPoint: String? = null,
                    @SerialName("tripPatternId") val tripPatternId: Int = -1,
                ) {
                    @Serializable
                    data class Stops(
                        @SerialName("Stop") val stop: List<Stop> = emptyList(),
                    ) {
                        @Serializable
                        data class Stop(
                            @SerialName("Code") val code: String = "",
                            @SerialName("Order") val order: Int = -1,
                            @SerialName("Time") val time: String = "",
                            @SerialName("sortingTime") val sortingTime: String = "",
                            @SerialName("IsMajor") val isMajor: Boolean = false,
                        )
                    }
                }
            }
        }
    }
}
