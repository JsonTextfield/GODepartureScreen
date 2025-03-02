package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.theme.barrie
import com.jsontextfield.departurescreen.ui.theme.lakeshoreEast
import com.jsontextfield.departurescreen.ui.theme.lakeshoreWest
import com.jsontextfield.departurescreen.ui.theme.richmondHill

class FakeGoTrainDataSource : IGoTrainDataSource {
    override suspend fun getTrains(): List<Train> {
        return listOf(
            Train(
                code = "BR",
                departureTimeString = "12:00",
                destination = "Allandale Waterfront GO",
                info = "Proceed / Attendez",
                platform = "3",
                color = barrie,
            ),
            Train(
                code = "LW",
                departureTimeString = "10:00",
                destination = "Niagara Falls Go (Via Rail Station)",
                info = "Wait / Attendez",
                id = "1959",
                platform = "-",
                color = lakeshoreWest,
            ),
            Train(
                code = "ST",
                departureTimeString = "11:00",
                destination = "Mount Joy GO",
                info = "Proceed / Attendez",
                platform = "7 & 8",
                color = lakeshoreEast,
            ),
            Train(
                code = "RH",
                departureTimeString = "13:00",
                destination = "Bloomington GO",
                info = "Proceed / Attendez",
                platform = "11 & 12",
                color = richmondHill,
            ),
            Train(
                code = "MI",
                departureTimeString = "13:00",
                destination = "Milton GO",
                platform = "2",
                color = richmondHill,
            ),
        )
    }
}