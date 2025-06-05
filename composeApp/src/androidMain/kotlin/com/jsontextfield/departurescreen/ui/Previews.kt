package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.jsontextfield.departurescreen.Train

private val sampleTrains = (0 until 7).map {
    Train(
        id = (it + 1000).toString(),
        code = String.format("%02d", it),
        departureTimeString = "10:00",
        destination = "Station $it",
        platform = "4 & 5",
    )
}

@PreviewFontScale
@PreviewScreenSizes
@Composable
fun TrainListPreview() {// Sample data for the previews
    TrainList(trains = sampleTrains, modifier = Modifier.fillMaxSize())
}