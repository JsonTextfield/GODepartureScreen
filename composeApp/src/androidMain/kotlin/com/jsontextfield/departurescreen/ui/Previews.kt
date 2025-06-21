package com.jsontextfield.departurescreen.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.menu.Action
import kotlinx.datetime.Instant

private val sampleTrains = List(11) {
    Train(
        id = (it + 1000).toString(),
        code = (it + 10).toString(),
        departureTime = Instant.fromEpochMilliseconds( it * 7894563L),
        destination = "Station $it",
        platform = "${it + 1} & ${it + 2}",
    )
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
fun AppPreview() {
    App(
        allTrains = sampleTrains,
        hiddenTrains = emptySet(),
        timeRemaining = 16000,
        actions = listOf(
            Action(
                icon = Icons.Rounded.FilterList,
                tooltip = "Filter",
            ),
            Action(
                icon = Icons.AutoMirrored.Rounded.Sort,
                tooltip = "Sort",
            ),
        ),
        shouldShowFilterDialog = false,
        onDismissFilterDialog = {},
        onSetHiddenTrains = {},
    )
}