package com.jsontextfield.departurescreen.core.entities

data class TripDetails(
    val id: String,
    val stops: List<Schedule>,
    val serviceGuarantee: String = "",
    val direction: String = "",
    val sameDirectionTripNumbers: Set<String> = emptySet(),
)
