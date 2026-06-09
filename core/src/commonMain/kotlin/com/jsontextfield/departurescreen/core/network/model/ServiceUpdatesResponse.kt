package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ServiceUpdatesResponse(
    @JsonNames("LastUpdated") val lastUpdated: String? = null,
    @JsonNames("TotalUpdates") val totalUpdates: Int = 0,
    @JsonNames("Notifications") val notifications: Notifications? = null,
    @JsonNames("Trains") val trains: Trains? = null,
    @JsonNames("Buses") val buses: Buses? = null,
    @JsonNames("Stations") val stations: Stations? = null,
    @JsonNames("TrainAnnouncements") val trainAnnouncements: Announcements? = null,
    @JsonNames("BusAnnouncements") val busAnnouncements: Announcements? = null,
) {
    @Serializable
    data class Notifications(
        @JsonNames("Notification") val notification: List<Notification> = emptyList(),
    )

    @Serializable
    data class Notification(
        @JsonNames("SubCategory") val subCategory: String? = null,
        @JsonNames("Code") val code: String? = null,
        @JsonNames("Name") val name: String? = null,
        @JsonNames("MessageSubject") val messageSubject: String? = null,
        @JsonNames("MessageBody") val messageBody: String? = null,
        @JsonNames("PostedDateTime") val postedDateTime: String? = null,
        @JsonNames("Rank") val rank: Int? = null,
        @JsonNames("Status") val status: String? = null,
        @JsonNames("ServiceMode") val serviceMode: String? = null,
        @JsonNames("TripNumbers") val tripNumbers: String? = null,
        @JsonNames("AffectedLineCodes") val affectedLineCodes: List<String> = emptyList(),
    )

    @Serializable
    data class Trains(
        @JsonNames("TotalUpdates") val totalUpdates: Int = 0,
        @JsonNames("Train") val train: List<Train> = emptyList(),
        @JsonNames("Status") val status: String? = null,
    ) {
        @Serializable
        data class Train(
            @JsonNames("CorridorName") val corridorName: String? = null,
            @JsonNames("CorridorCode") val corridorCode: String? = null,
            @JsonNames("Notifications") val notifications: Notifications? = null,
            @JsonNames("TotalUpdates") val totalUpdates: Int = 0,
            @JsonNames("LineColour") val lineColour: String? = null,
        )
    }

    @Serializable
    data class Buses(
        @JsonNames("TotalUpdates") val totalUpdates: Int = 0,
        @JsonNames("Bus") val bus: List<Bus> = emptyList(),
        @JsonNames("Status") val status: String? = null,
    ) {
        @Serializable
        data class Bus(
            @JsonNames("RouteName") val routeName: String? = null,
            @JsonNames("RouteNumber") val routeNumber: String? = null,
            @JsonNames("Notifications") val notifications: Notifications? = null,
            @JsonNames("TotalUpdates") val totalUpdates: Int = 0,
            @JsonNames("LineColour") val lineColour: String? = null,
        )
    }

    @Serializable
    data class Stations(
        @JsonNames("TotalUpdates") val totalUpdates: Int = 0,
        @JsonNames("Station") val station: List<Station> = emptyList(),
        @JsonNames("Status") val status: String? = null,
    ) {
        @Serializable
        data class Station(
            @JsonNames("StationName") val stationName: String? = null,
            @JsonNames("StationCode") val stationCode: String? = null,
            @JsonNames("Notifications") val notifications: Notifications? = null,
            @JsonNames("TotalUpdates") val totalUpdates: Int = 0,
        )
    }

    @Serializable
    data class Announcements(
        @JsonNames("Notification") val notification: List<Notification> = emptyList(),
        @JsonNames("TotalUpdates") val totalUpdates: Int = 0,
    )
}
