@file:OptIn(ExperimentalSerializationApi::class)

package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExceptionsResponse(
    @SerialName("Metadata") val metadata: Metadata? = null,
    @SerialName("Trip") val trip: List<Trip> = emptyList(),
) {
    @Serializable
    data class Trip(
        @SerialName("TripNumber") val tripNumber: String = "",
        @SerialName("TripName") val tripName: String = "",
        @SerialName("IsCancelled") val isCancelled: String = "",
        @SerialName("IsOverride") val isOverride: String = "",
        @SerialName("Stop") val stop: List<Stop>? = null,
    ) {
        @Serializable
        data class Stop(
            @SerialName("Order") val order: Int = 0,
            @SerialName("ID") val id: String = "",
            @SerialName("SchArrival") val schArrival: String? = null,
            @SerialName("Name") val name: String = "",
            @SerialName("IsStopping") val isStopping: String? = null,
            @SerialName("IsCancelled") val isCancelled: String? = null,
            @SerialName("IsOverride") val isOverride: String? = null,
            @SerialName("Code") val code: String = "",
            @SerialName("ActualTime") val actualTime: String? = null,
            @SerialName("ServiceType") val serviceType: String? = null,
        )
    }
}