package com.jsontextfield.departurescreen

data class Train(
    val code: String = "",
    val name: String = "",
    val destination: String = "",
    val platform: String = "",
    val departureTime: String = "",
    val color: Int = 0xFF45AAFF.toInt(),
    val tripOrder: Int = 0
)
