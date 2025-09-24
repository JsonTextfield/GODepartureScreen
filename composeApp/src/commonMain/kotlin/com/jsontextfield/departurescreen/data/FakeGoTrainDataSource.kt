@file:OptIn(ExperimentalUuidApi::class)

package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.entities.Alert
import com.jsontextfield.departurescreen.entities.Station
import com.jsontextfield.departurescreen.entities.Train
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FakeGoTrainDataSource : IGoTrainDataSource {
    var trains: List<Train> = listOf(
        Train(
            id = Uuid.random().toString(),
            code = "BR",
            destination = "Allandale Waterfront GO",
            platform = "3",
        ),
        Train(
            id = Uuid.random().toString(),
            code = "LW",
            destination = "Niagara Falls Go (Via Rail Station)",
            info = "Wait / Attendez",
            platform = "-",
        ),
        Train(
            id = Uuid.random().toString(),
            code = "ST",
            destination = "Mount Joy GO",
            info = "Proceed / Attendez",
            platform = "7 & 8",
        ),
        Train(
            id = Uuid.random().toString(),
            code = "RH",
            destination = "Bloomington GO",
            info = "Proceed / Attendez",
            platform = "11 & 12",
        ),
        Train(
            id = Uuid.random().toString(),
            code = "MI",
            destination = "Milton GO",
            platform = "9",
        ),
    )

    override suspend fun getTrains(stationCode: String): List<Train> {
        return trains
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