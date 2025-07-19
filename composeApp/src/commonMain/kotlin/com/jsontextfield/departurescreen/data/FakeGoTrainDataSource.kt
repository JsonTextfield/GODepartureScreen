@file:OptIn(ExperimentalUuidApi::class)

package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.theme.barrie
import com.jsontextfield.departurescreen.ui.theme.lakeshoreWest
import com.jsontextfield.departurescreen.ui.theme.milton
import com.jsontextfield.departurescreen.ui.theme.richmondHill
import com.jsontextfield.departurescreen.ui.theme.stouffville
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FakeGoTrainDataSource : IGoTrainDataSource {
    var trains: List<Train> = listOf(
        Train(
            id = Uuid.random().toString(),
            code = "BR",
            departureTime = Instant.DISTANT_FUTURE,
            destination = "Allandale Waterfront GO",
            platform = "3",
            color = barrie,
        ),
        Train(
            id = Uuid.random().toString(),
            code = "LW",
            departureTime = Instant.DISTANT_PAST,
            destination = "Niagara Falls Go (Via Rail Station)",
            info = "Wait / Attendez",
            platform = "-",
            color = lakeshoreWest,
        ),
        Train(
            id = Uuid.random().toString(),
            code = "ST",
            departureTime = Instant.fromEpochMilliseconds(0),
            destination = "Mount Joy GO",
            info = "Proceed / Attendez",
            platform = "7 & 8",
            color = stouffville,
        ),
        Train(
            id = Uuid.random().toString(),
            code = "RH",
            departureTime = Instant.fromEpochMilliseconds(30L * 366 * 24 * 60 * 60 * 1000),
            destination = "Bloomington GO",
            info = "Proceed / Attendez",
            platform = "11 & 12",
            color = richmondHill,
        ),
        Train(
            id = Uuid.random().toString(),
            code = "MI",
            departureTime = Clock.System.now(),
            destination = "Milton GO",
            platform = "9",
            color = milton,
        ),
    )

    override suspend fun getTrains(): List<Train> {
        return trains
    }
}