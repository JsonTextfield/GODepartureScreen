@file:OptIn(ExperimentalSerializationApi::class)

package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class TripUpdatesResponse(
    val header: Header,
    val entity: List<Entity>
) {
    @Serializable
    data class Header(
        @JsonNames("gtfs_realtime_version") val gtfsRealtimeVersion: String? = null,
        val incrementality: String? = null,
        val timestamp: Long? = null
    )

    @Serializable
    data class Entity(
        val id: String,
        @JsonNames("is_deleted") val isDeleted: Boolean = false,
        @JsonNames("trip_update") val tripUpdate: TripUpdate? = null,
        val vehicle: Vehicle? = null,
        val alert: String? = null,
    ) {
        @Serializable
        data class TripUpdate(
            val trip: Trip,
            val vehicle: Vehicle? = null,
            @JsonNames("stop_time_update") val stopTimeUpdate: List<StopTimeUpdate>,
            val timestamp: Long? = null,
            val delay: Int? = null
        ) {
            @Serializable
            data class Trip(
                @JsonNames("trip_id") val tripId: String,
                @JsonNames("route_id") val routeId: String,
                @JsonNames("direction_id") val directionId: Int = 0,
                @JsonNames("start_time") val startTime: String,
                @JsonNames("start_date") val startDate: String,
                @JsonNames("schedule_relationship") val scheduleRelationship: String? = null
            )

            @Serializable
            data class StopTimeUpdate(
                @JsonNames("stop_id")
                val stopId: String,
                val departure: Departure? = null,
                val arrival: Arrival? = null,
                @JsonNames("schedule_relationship")
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
            @JsonNames("license_plate") val licensePlate: String
        )
    }
}
