package com.jsontextfield.departurescreen.entities

data class Station(
    val name: String,
    val code: String,
    val type: String,
    val isEnabled: Boolean = true,
    val isFavourite: Boolean = true,
)
