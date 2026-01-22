package com.jsontextfield.departurescreen.core.entities

import com.jsontextfield.departurescreen.core.ui.StopType

data class Stop(
    val name: String,
    val code: String,
    val types: Set<StopType> = emptySet(),
    val isEnabled: Boolean = true,
    val isFavourite: Boolean = false,
)
