package com.jsontextfield.departurescreen.core.entities

data class Station(
    val name: String,
    val code: String,
    val type: String,
    val isEnabled: Boolean = true,
    val isFavourite: Boolean = false,
)
