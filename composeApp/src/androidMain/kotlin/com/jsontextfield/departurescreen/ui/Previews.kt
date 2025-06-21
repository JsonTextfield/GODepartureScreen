package com.jsontextfield.departurescreen.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.jsontextfield.departurescreen.Train

private val sampleTrains = (0 until 7).map {
    Train(
        id = (it + 1000).toString(),
        code = (it + 10).toString(),
        departureTimeString = "12:34",
        destination = "Station $it",
        platform = "${it + 1} & ${it + 2}",
    )
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@PreviewDynamicColors
@Composable
fun AppPreview() {
    App(
        allTrains = sampleTrains,
        hiddenTrains = emptySet(),
        timeRemaining = 16000,
        actions = emptyList(),
        shouldShowFilterDialog = false,
        onDismissFilterDialog = {},
        onSetHiddenTrains = {},
    )
}