package com.jsontextfield.departurescreen.core.entities

data class Station(
    val name: String,
    val code: String,
    val type: String,
    val isEnabled: Boolean = true,
    val isFavourite: Boolean = false,
)

fun List<Station>.toCombinedStation(): CombinedStation {
    return CombinedStation(
        name = first().name,
        codes = map { it.code }.toSet(),
        types = map { it.type }.toSet(),
        isFavourite = any { it.isFavourite },
    )
}

data class CombinedStation(
    val name: String,
    val codes: Set<String>,
    val types: Set<String>,
    val isEnabled: Boolean = true,
    val isFavourite: Boolean = false,
)
