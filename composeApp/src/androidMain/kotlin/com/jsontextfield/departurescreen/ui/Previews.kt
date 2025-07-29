package com.jsontextfield.departurescreen.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.menu.Action
import com.jsontextfield.departurescreen.ui.theme.trainColours

private val sampleTrains = List(11) {
    Train(
        id = (37 * it + 1000).toString(),
        code = "${Char(65 + it)}${Char(68 + it)}",
        destination = "Station",
        platform = "${it + 1} & ${it + 2}",
        color = trainColours.values.random(),
        isCancelled = it % 3 == 0,
    )
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
fun AppPreview() {
    App(
        allTrains = sampleTrains,
        visibleTrains = emptySet(),
        timeRemaining = 16000,
        actions = listOf(
            Action(
                icon = Icons.AutoMirrored.Rounded.Sort,
                tooltip = "Sort",
            ),
        ),
        onSetVisibleTrains = {},
    )
}