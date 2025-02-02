package com.jsontextfield.departurescreen

import androidx.compose.ui.graphics.Color

data class Train(
    val code: String = "",
    val name: String = "",
    val destination: String = "",
    val platform: String = "",
    val departureTime: String = "",
    val color: Color = Color(0xFF45AAFF),
    val tripOrder: Int = 0,
    val info: String = "",
)
