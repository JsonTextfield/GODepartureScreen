package com.jsontextfield.departurescreen.core.entities

import com.jsontextfield.departurescreen.core.ui.StationType

data class Station(
    val name: String,
    val code: String,
    val types: Set<StationType> = emptySet(),
    val isEnabled: Boolean = true,
    val isFavourite: Boolean = false,
)
