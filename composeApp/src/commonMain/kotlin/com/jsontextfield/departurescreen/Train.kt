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
    val color: Color = Color(0xFF45AAFF),
    val tripOrder: Int = 0,
    val info: String = "",
) {
    val hasArrived: Boolean
        get() = platform.isNotBlank() && platform != "-"
    val isExpress: Boolean
        get() = try {
            id[2].digitToInt() >= 5
        } catch (e: Exception) {
            false
        }
}
