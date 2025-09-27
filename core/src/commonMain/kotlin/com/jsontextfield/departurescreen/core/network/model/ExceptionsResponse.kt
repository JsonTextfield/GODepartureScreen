@file:OptIn(ExperimentalSerializationApi::class)

package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ExceptionsResponse(
    @JsonNames("Metadata") val metadata: Metadata? = null,
    @JsonNames("Trip") val trip: List<Trip> = emptyList(),
) {
    @Serializable
    data class Trip(
        @JsonNames("TripNumber") val tripNumber: String = "",
        @JsonNames("TripName") val tripName: String = "",
        @JsonNames("IsCancelled") val isCancelled: String = "",
        @JsonNames("IsOverride") val isOverride: String = "",
        @JsonNames("Stop") val stop: List<Stop>? = null,
    ) {
        @Serializable
        data class Stop(
            @JsonNames("Order") val order: Int = 0,
            @JsonNames("ID") val id: String = "",
            @JsonNames("SchArrival") val schArrival: String? = null,
            @JsonNames("Name") val name: String = "",
            @JsonNames("IsStopping") val isStopping: String? = null,
            @JsonNames("IsCancelled") val isCancelled: String? = null,
            @JsonNames("IsOverride") val isOverride: String? = null,
            @JsonNames("Code") val code: String = "",
            @JsonNames("ActualTime") val actualTime: String? = null,
            @JsonNames("ServiceType") val serviceType: String? = null,
        )
    }
}