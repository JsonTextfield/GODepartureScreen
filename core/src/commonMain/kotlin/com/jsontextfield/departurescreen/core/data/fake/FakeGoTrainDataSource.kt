@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.data.fake

import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.entities.TripDetails
import com.jsontextfield.departurescreen.core.ui.StationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FakeGoTrainDataSource : IGoTrainDataSource {

    var stations: List<Station> = listOf(
        Station(
            code = "UN",
            name = "Union Station",
            types = setOf(StationType.TRAIN),
        )
    )
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

    var serviceAlerts: List<Alert> = emptyList()

    var informationAlerts: List<Alert> = emptyList()


    override suspend fun getTrips(stationCode: String): List<Trip> {
        return trips
    }

    override suspend fun getTripDetails(tripNumber: String) : TripDetails {
        TODO("Not yet implemented")
    }

    override fun getServiceAlerts(): Flow<List<Alert>> {
        return flowOf(serviceAlerts)
    }

    override fun getInformationAlerts(): Flow<List<Alert>> {
        return flowOf(informationAlerts)
    }

    override fun getMarketingAlerts(): Flow<List<Alert>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllStations(): List<Station> {
        return stations
    }
}