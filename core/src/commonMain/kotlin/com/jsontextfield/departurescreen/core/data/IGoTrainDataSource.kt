package com.jsontextfield.departurescreen.core.data

import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.entities.TripDetails
import kotlinx.coroutines.flow.Flow

interface IGoTrainDataSource {
    suspend fun getTrips(stationCode: String): List<Trip>

    suspend fun getTripDetails(tripNumber: String) : TripDetails?

    fun getServiceAlerts(): Flow<List<Alert>>

    fun getInformationAlerts(): Flow<List<Alert>>

    fun getMarketingAlerts(): Flow<List<Alert>>

    suspend fun getAllStations(): List<Station>
}