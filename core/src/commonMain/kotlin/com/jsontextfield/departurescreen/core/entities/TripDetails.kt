@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.entities

import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.byUnicodePattern
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class TripDetails(
    val id: String,
    val stops: List<Schedule>,
    val serviceGuarantee: String = "",
)

data class Schedule(
    val name: String,
    val code: String,
    val time: Instant = Instant.fromEpochMilliseconds(0),
    private val lastUpdated: Instant = Instant.fromEpochMilliseconds(0),
) {
    val relativeDepartureTime: Int = (time - lastUpdated).toInt(DurationUnit.MINUTES)
    val twentyFourHourDepartureTime: String = time.format(DateTimeComponents.Format {
        byUnicodePattern("HH:mm")
    })
}
