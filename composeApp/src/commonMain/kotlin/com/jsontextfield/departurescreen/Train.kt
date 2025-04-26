package com.jsontextfield.departurescreen

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Instant

data class Train(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val destination: String = "",
    val platform: String = "",
    val departureTime: Instant = Instant.fromEpochMilliseconds(0),
    val departureTimeString: String = "",
    val color: Color = Color.Gray,
    val tripOrder: Int = 0,
    val info: String = "",
) {
    val hasArrived = platform.isNotBlank() && platform != "-"
    val isExpress = id[2] in "56789"
}
