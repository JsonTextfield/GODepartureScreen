package com.jsontextfield.departurescreen.entities

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit

data class Train(
    val id: String,
    val code: String = "",
    val name: String = "",
    val destination: String = "",
    val platform: String = "",
    val departureTime: Instant = Instant.fromEpochMilliseconds(0),
    private val lastUpdated: Instant = Instant.fromEpochMilliseconds(0),
    val color: Color = Color.Gray,
    val tripOrder: Int = 0,
    val info: String = "",
    val isVisible: Boolean = true,
    val isCancelled: Boolean = false,
) {
    val departureDiffMinutes: Int = (departureTime - lastUpdated).toInt(DurationUnit.MINUTES)
    val hasPlatform: Boolean = platform.isNotBlank() && platform != "-"
    val isExpress: Boolean = 'X' in id
}
