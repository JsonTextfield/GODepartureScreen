@file:OptIn(ExperimentalUuidApi::class)

package com.jsontextfield.departurescreen.core.data

import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FakeGoTrainDataSource : IGoTrainDataSource {
    var trips: List<Trip> = listOf(
        Trip(
            id = Uuid.random().toString(),
            code = "BR",
            destination = "Allandale Waterfront GO",
            platform = "3",
        ),
        Trip(
            id = Uuid.random().toString(),
            code = "LW",
            destination = "Niagara Falls Go (Via Rail Station)",
            info = "Wait / Attendez",
            platform = "-",
        ),
        Trip(
            id = Uuid.random().toString(),
            code = "ST",
            destination = "Mount Joy GO",
            info = "Proceed / Attendez",
            platform = "7 & 8",
        ),
        Trip(
            id = Uuid.random().toString(),
            code = "RH",
            destination = "Bloomington GO",
            info = "Proceed / Attendez",
            platform = "11 & 12",
        ),
        Trip(
            id = Uuid.random().toString(),
            code = "MI",
            destination = "Milton GO",
            platform = "9",
        ),
    )

    override suspend fun getTrains(stationCode: String): List<Trip> {
        return trips
    }

    override suspend fun getServiceAlerts(): List<Alert> {
        return emptyList()
    }

    override suspend fun getInformationAlerts(): List<Alert> {
        return emptyList()
    }

    override suspend fun getAllStations(): List<Station> {
        return emptyList()
    }
}