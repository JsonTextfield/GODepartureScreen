package com.jsontextfield.departurescreen

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDateTime

data class Train(
    val id: String = "",
    val code: String = "",
    val name: String = "",
    val destination: String = "",
    val platform: String = "",
    val departureTime: LocalDateTime = LocalDateTime(2024, 1, 1, 12, 0, 0, 0),
    val departureTimeString: String = "",
    val color: Color = Color(0xFF45AAFF),
    val tripOrder: Int = 0,
    val info: String = "",
) {
    val hasArrived: Boolean
        get() = platform.isNotBlank() && platform != "-"
    val isExpress: Boolean
        get() = try {
            id.substring(2, 3).toInt() >= 5
        } catch (e: Exception) {
            false
        }
}
