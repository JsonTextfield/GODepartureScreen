package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.entities.Alert
import com.jsontextfield.departurescreen.entities.Station
import com.jsontextfield.departurescreen.entities.Trip

interface IGoTrainDataSource {
    suspend fun getTrains(stationCode: String) : List<Trip>

    suspend fun getServiceAlerts() : List<Alert>

    suspend fun getInformationAlerts() : List<Alert>

    suspend fun getAllStations(): List<Station>
}