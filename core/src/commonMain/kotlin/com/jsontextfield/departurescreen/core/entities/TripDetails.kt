package com.jsontextfield.departurescreen.core.entities

import androidx.compose.runtime.Immutable

@Immutable
data class TripDetails(
    val id: String,
    val stops: List<Schedule>,
    val serviceGuarantee: String = "",
)
