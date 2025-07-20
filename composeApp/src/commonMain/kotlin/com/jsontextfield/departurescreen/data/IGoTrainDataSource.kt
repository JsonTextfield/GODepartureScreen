package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.Alert
import com.jsontextfield.departurescreen.Train

interface IGoTrainDataSource {
    suspend fun getTrains() : List<Train>

    suspend fun getServiceAlerts() : List<Alert>

    suspend fun getInformationAlerts() : List<Alert>
}