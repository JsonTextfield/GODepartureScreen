package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.Train

fun interface IGoTrainDataSource {
    suspend fun getTrains() : List<Train>
}