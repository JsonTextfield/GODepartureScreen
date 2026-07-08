package com.jsontextfield.departurescreen.core.entities

import androidx.compose.runtime.Immutable
import com.jsontextfield.departurescreen.core.ui.StopType

@Immutable
data class Stop(
    val name: String,
    val code: String,
    val types: Set<StopType> = emptySet(),
    val isEnabled: Boolean = true,
    val isFavourite: Boolean = false,
)
