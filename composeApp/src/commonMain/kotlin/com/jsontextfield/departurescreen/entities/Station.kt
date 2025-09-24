package com.jsontextfield.departurescreen.entities

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
        codes = map { it.code },
        types = map { it.type },
        isFavourite = any { it.isFavourite },
    )
}

data class CombinedStation(
    val name: String,
    val codes: List<String>,
    val types: List<String>,
    val isEnabled: Boolean = true,
    val isFavourite: Boolean = false,
)
