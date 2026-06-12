package com.jsontextfield.departurescreen.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ServiceUpdatesResponse(
    @SerialName("LastUpdated") val lastUpdated: String? = null,
    @SerialName("TotalUpdates") val totalUpdates: Int = 0,
    @SerialName("Notifications") val notifications: Notifications? = null,
    @SerialName("Trains") val trains: Trains? = null,
    @SerialName("Buses") val buses: Buses? = null,
    @SerialName("Stations") val stations: Stations? = null,
    @SerialName("TrainAnnouncements") val trainAnnouncements: Announcements? = null,
    @SerialName("BusAnnouncements") val busAnnouncements: Announcements? = null,
) {
    @Serializable
    data class Notifications(
        @SerialName("Notification") val notification: List<Notification> = emptyList(),
    )

    @Serializable
    data class Notification(
        @SerialName("SubCategory") val subCategory: String? = null,
        @SerialName("Code") val code: String? = null,
        @SerialName("Name") val name: String? = null,
        @SerialName("MessageSubject") val messageSubject: String? = null,
        @SerialName("MessageBody") val messageBody: String? = null,
        @SerialName("PostedDateTime") val postedDateTime: String? = null,
        @SerialName("Rank") val rank: Int? = null,
        @SerialName("Status") val status: String? = null,
        @SerialName("ServiceMode") val serviceMode: String? = null,
        @SerialName("TripNumbers") val tripNumbers: String? = null,
        @SerialName("AffectedLineCodes") val affectedLineCodes: List<String> = emptyList(),
    )

    @Serializable
    data class Trains(
        @SerialName("TotalUpdates") val totalUpdates: Int = 0,
        @SerialName("Train") val train: List<Train> = emptyList(),
        @SerialName("Status") val status: String? = null,
    ) {
        @Serializable
        data class Train(
            @SerialName("CorridorName") val corridorName: String? = null,
            @SerialName("CorridorCode") val corridorCode: String? = null,
            @SerialName("Notifications") val notifications: Notifications? = null,
            @SerialName("TotalUpdates") val totalUpdates: Int = 0,
            @SerialName("LineColour") val lineColour: String? = null,
        )
    }

    @Serializable
    data class Buses(
        @SerialName("TotalUpdates") val totalUpdates: Int = 0,
        @SerialName("Bus") val bus: List<Bus> = emptyList(),
        @SerialName("Status") val status: String? = null,
    ) {
        @Serializable
        data class Bus(
            @SerialName("RouteName") val routeName: String? = null,
            @SerialName("RouteNumber") val routeNumber: String? = null,
            @SerialName("Notifications") val notifications: Notifications? = null,
            @SerialName("TotalUpdates") val totalUpdates: Int = 0,
            @SerialName("LineColour") val lineColour: String? = null,
        )
    }

    @Serializable
    data class Stations(
        @SerialName("TotalUpdates") val totalUpdates: Int = 0,
        @SerialName("Station") val station: List<Station> = emptyList(),
        @SerialName("Status") val status: String? = null,
    ) {
        @Serializable
        data class Station(
            @SerialName("StationName") val stationName: String? = null,
            @SerialName("StationCode") val stationCode: String? = null,
            @SerialName("Notifications") val notifications: Notifications? = null,
            @SerialName("TotalUpdates") val totalUpdates: Int = 0,
        )
    }

    @Serializable
    data class Announcements(
        @SerialName("Notification") val notification: List<Notification> = emptyList(),
        @SerialName("TotalUpdates") val totalUpdates: Int = 0,
    )
}
