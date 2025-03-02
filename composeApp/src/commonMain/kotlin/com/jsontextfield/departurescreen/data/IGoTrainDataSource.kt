package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.Train

interface IGoTrainDataSource {
    suspend fun getTrains() : List<Train>
}