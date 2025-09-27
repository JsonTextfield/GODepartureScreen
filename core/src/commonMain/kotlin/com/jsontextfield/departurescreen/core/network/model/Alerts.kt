package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Alerts(
    @JsonNames("Metadata") val metadata: Metadata? = null,
    @JsonNames("Messages") val messages: Messages? = null,
) {
    @Serializable
    data class Messages(
        @JsonNames("Message") val message: List<Message> = emptyList(),
    ) {
        @Serializable
        data class Message(
            @JsonNames("Code") val code: String,
            @JsonNames("ParentCode") val parentCode: String? = null,
            @JsonNames("Status") val status: String? = "",
            @JsonNames("PostedDateTime") val postedDateTime: String = "",
            @JsonNames("SubjectEnglish") val subjectEnglish: String? = "",
            @JsonNames("SubjectFrench") val subjectFrench: String? = "",
            @JsonNames("BodyEnglish") val bodyEnglish: String? = "",
            @JsonNames("BodyFrench") val bodyFrench: String? = "",
            @JsonNames("Category") val category: String? = "",
            @JsonNames("SubCategory") val subCategory: String? = null,
            @JsonNames("Lines") val lines: List<Line> = emptyList(),
            @JsonNames("Stops") val stops: List<Stop> = emptyList(),
            @JsonNames("Trips") val trips: List<Trip> = emptyList(),
        ) {
            @Serializable
            data class Line(
                @JsonNames("Code") val code: String = "",
            )

            @Serializable
            data class Stop(
                @JsonNames("Name") val name: String? = null,
                @JsonNames("Code") val code: String = "",
            )

            @Serializable
            data class Trip(
                @JsonNames("TripNumber") val tripNumber: String = "",
            )
        }
    }
}