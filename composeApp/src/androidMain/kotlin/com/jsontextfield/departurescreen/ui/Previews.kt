package com.jsontextfield.departurescreen.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.theme.lineColours

private val sampleTrips = List(11) {
    Trip(
        id = (37 * it + 1000).toString(),
        code = "${Char(65 + it)}${Char(68 + it)}",
        destination = "Station",
        platform = "${it + 1} & ${it + 2}",
        color = lineColours.values.random(),
        isCancelled = it % 3 == 0,
    )
}

@PreviewLightDark
@PreviewFontScale
@PreviewScreenSizes
@Composable
fun AppPreview() {
//    App(
//        uiState = UIState(
//            status = Status.LOADED,
//            _allTrips = sampleTrips,
//            visibleTrains = emptySet(),
//            selectedStation = CombinedStation("Union GO Station", listOf("UN"), types = listOf("Train Station")),
//        ),
//        timeRemaining = 16000,
//        actions = listOf(
//            Action(
//                icon = Icons.AutoMirrored.Rounded.Sort,
//                tooltip = "Sort",
//            ),
//        ),
//    )
}