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
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getTrains(): List<Train> {
        return listOf(
            Train(
                id = Uuid.random().toString(),
                code = "BR",
                departureTime = Instant.DISTANT_FUTURE,
                departureTimeString = "12:34",
                destination = "Allandale Waterfront GO",
                platform = "3",
                color = barrie,
            ),
            Train(
                id = Uuid.random().toString(),
                code = "LW",
                departureTime = Instant.DISTANT_PAST,
                departureTimeString = "10:00",
                destination = "Niagara Falls Go (Via Rail Station)",
                info = "Wait / Attendez",
                platform = "-",
                color = lakeshoreWest,
            ),
            Train(
                id = Uuid.random().toString(),
                code = "ST",
                departureTime = Instant.fromEpochMilliseconds(0),
                departureTimeString = "11:00",
                destination = "Mount Joy GO",
                info = "Proceed / Attendez",
                platform = "7 & 8",
                color = stouffville,
            ),
            Train(
                id = Uuid.random().toString(),
                code = "RH",
                departureTime = Instant.fromEpochMilliseconds( 30L * 366 * 24 * 60 * 60 * 1000),
                departureTimeString = "13:00",
                destination = "Bloomington GO",
                info = "Proceed / Attendez",
                platform = "11 & 12",
                color = richmondHill,
            ),
            Train(
                id = Uuid.random().toString(),
                code = "MI",
                departureTime = Clock.System.now(),
                departureTimeString = "13:00",
                destination = "Milton GO",
                platform = "9",
                color = milton,
            ),
        )
    }
}