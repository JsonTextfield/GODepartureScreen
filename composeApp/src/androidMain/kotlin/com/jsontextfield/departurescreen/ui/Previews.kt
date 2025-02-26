package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.theme.richmondHill

private val sampleTrains = (0 until 7).map {
    Train(
        code = String.format("%02d", it),
        departureTimeString = "10:00",
        destination = "Station $it",
        info = "Wait / Attendez"
    )
}

@Preview
@Preview(
    device = "spec:width=640dp,height=360dp"
)
@Preview(
    device = "spec:width=1080dp,height=640dp",
)
@Composable
fun TrainListPreview() {// Sample data for the previews
    TrainList(trains = sampleTrains, modifier = Modifier.fillMaxSize())
}

@Preview
@Composable
fun TrainListItemPreview() {
    val train = Train(
        destination = "Bloomington GO",
        platform = "4 & 5",
        code = "RH",
        departureTimeString = "12:34",
        info = "Wait / Attendez",
        color = richmondHill,
    )
    TrainListItem(train)
}