package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.theme.barrie
import com.jsontextfield.departurescreen.ui.theme.lakeshoreEast
import com.jsontextfield.departurescreen.ui.theme.lakeshoreWest
import com.jsontextfield.departurescreen.ui.theme.richmondHill

class FakeGoTrainDataSource : IGoTrainDataSource {
    override suspend fun getTrains(apiKey: String): List<Train> {
        return listOf(
            Train(
                code = "LW",
                departureTimeString = "10:00",
                destination = "Station A",
                info = "Wait / Attendez",
                platform = "-",
                color = lakeshoreWest,
            ),
            Train(
                code = "LE",
                departureTimeString = "11:00",
                destination = "Station B",
                info = "Proceed / Attendez",
                platform = "7 & 8",
                color = lakeshoreEast,
            ),
            Train(
                code = "BR",
                departureTimeString = "12:00",
                destination = "Station C",
                info = "Proceed / Attendez",
                platform = "3",
                color = barrie,
            ),
            Train(
                code = "RH",
                departureTimeString = "13:00",
                destination = "Station D",
                info = "Proceed / Attendez",
                platform = "11 & 12",
                color = richmondHill,
            ),
        )
    }
}