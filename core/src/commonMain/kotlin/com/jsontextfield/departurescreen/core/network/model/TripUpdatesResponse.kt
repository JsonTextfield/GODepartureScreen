@file:OptIn(ExperimentalSerializationApi::class)

package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TripUpdatesResponse(
    val header: Header,
    val entity: List<Entity>
) {
    @Serializable
    data class Header(
        @SerialName("gtfs_realtime_version") val gtfsRealtimeVersion: String? = null,
        val incrementality: String? = null,
        val timestamp: Long? = null
    )

    @Serializable
    data class Entity(
        val id: String,
        @SerialName("is_deleted") val isDeleted: Boolean = false,
        @SerialName("trip_update") val tripUpdate: TripUpdate? = null,
        val vehicle: Vehicle? = null,
        val alert: String? = null,
    ) {
        @Serializable
        data class TripUpdate(
            val trip: Trip,
            val vehicle: Vehicle? = null,
            @SerialName("stop_time_update") val stopTimeUpdate: List<StopTimeUpdate>,
            val timestamp: Long? = null,
            val delay: Int? = null
        ) {
            @Serializable
            data class Trip(
                @SerialName("trip_id") val tripId: String,
                @SerialName("route_id") val routeId: String,
                @SerialName("direction_id") val directionId: Int = 0,
                @SerialName("start_time") val startTime: String,
                @SerialName("start_date") val startDate: String,
                @SerialName("schedule_relationship") val scheduleRelationship: String? = null
            )

            @Serializable
            data class StopTimeUpdate(
                @SerialName("stop_id")
                val stopId: String,
                val departure: Departure? = null,
                val arrival: Arrival? = null,
                @SerialName("schedule_relationship")
                val scheduleRelationship: String? = null
            ) {
                @Serializable
                data class Departure(
                    val delay: Int = 0,
                    val time: Long = 0,
                    val uncertainty: Int = 0
                )

                @Serializable
                data class Arrival(
                    val delay: Int? = null,
                    val time: Long? = null,
                    val uncertainty: Int? = null
                )
            }
        }

        @Serializable
        data class Vehicle(
            val id: String,
            val label: String,
            @SerialName("license_plate") val licensePlate: String
        )
    }
}
