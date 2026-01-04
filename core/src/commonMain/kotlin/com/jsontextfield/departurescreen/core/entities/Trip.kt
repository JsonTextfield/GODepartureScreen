package com.jsontextfield.departurescreen.core.entities

import androidx.compose.ui.graphics.Color
import kotlin.time.DurationUnit
import kotlin.time.Instant

data class Trip(
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
    val isBus: Boolean = false,
    val cars: String? = null,
    val busType: String? = null,
) {
    val departureDiffMinutes: Int = (departureTime - lastUpdated).toInt(DurationUnit.MINUTES)
    val hasPlatform: Boolean = platform.isNotBlank() && platform != "-"
    val isExpress: Boolean = 'X' in id
}
