package com.jsontextfield.departurescreen

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

data class Train(
    val id: String,
    val code: String = "",
    val name: String = "",
    val destination: String = "",
    val platform: String = "",
    val departureTime: Instant = Instant.fromEpochMilliseconds(0),
    val color: Color = Color.Gray,
    val tripOrder: Int = 0,
    val info: String = "",
    val isVisible: Boolean = true,
) {
    val departureTimeString = departureTime.format(outFormatter)
    val hasArrived = platform.isNotBlank() && platform != "-"
    val isExpress = id[2] in "56789"

    companion object {
        @OptIn(FormatStringsInDatetimeFormats::class)
        val outFormatter = DateTimeComponents.Format {
            byUnicodePattern("HH:mm")
        }
    }
}
