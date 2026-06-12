package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlertsResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("Messages") val messages: Messages? = null,
) {
    @Serializable
    data class Messages(
        @SerialName("Message") val message: List<Message> = emptyList(),
    ) {
        @Serializable
        data class Message(
            @SerialName("Code") val code: String,
            @SerialName("ParentCode") val parentCode: String? = null,
            @SerialName("Status") val status: String? = "",
            @SerialName("PostedDateTime") val postedDateTime: String = "",
            @SerialName("SubjectEnglish") val subjectEnglish: String? = "",
            @SerialName("SubjectFrench") val subjectFrench: String? = "",
            @SerialName("BodyEnglish") val bodyEnglish: String? = "",
            @SerialName("BodyFrench") val bodyFrench: String? = "",
            @SerialName("Category") val category: String? = "",
            @SerialName("SubCategory") val subCategory: String? = null,
            @SerialName("Lines") val lines: List<Line> = emptyList(),
            @SerialName("Stops") val stops: List<Stop> = emptyList(),
            @SerialName("Trips") val trips: List<Trip> = emptyList(),
        ) {
            @Serializable
            data class Line(
                @SerialName("Code") val code: String = "",
            )

            @Serializable
            data class Stop(
                @SerialName("Name") val name: String? = null,
                @SerialName("Code") val code: String = "",
            )

            @Serializable
            data class Trip(
                @SerialName("TripNumber") val tripNumber: String = "",
            )
        }
    }
}