package com.jsontextfield.departurescreen.core.data

import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip

interface IGoTrainDataSource {
    suspend fun getTrips(stationCode: String) : List<Trip>

    suspend fun getServiceAlerts() : List<Alert>

    suspend fun getInformationAlerts() : List<Alert>

    suspend fun getAllStations(): List<Station>
}