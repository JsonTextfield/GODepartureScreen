@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.entities

import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Alert(
    val id: String,
    val date: Instant = Instant.fromEpochMilliseconds(0),
    val affectedLines: List<String> = emptyList(),
    val affectedStops: List<String> = emptyList(),
    private val subjectEn: String = "",
    private val subjectFr: String = "",
    private val bodyEn: String = "",
    private val bodyFr: String = "",
    val isRead: Boolean = false,
) {
    @OptIn(FormatStringsInDatetimeFormats::class)
    val dateDifference: Duration =
        Clock.System.now().toLocalDateTime(TimeZone.of("America/Toronto")).toInstant(TimeZone.UTC) - date

    fun getSubject(language: String): String {
        return subjectFr.takeIf { "fr" in language && it.isNotEmpty() } ?: subjectEn
    }

    fun getBody(language: String): String {
        return bodyFr.takeIf { "fr" in language && it.isNotEmpty() } ?: bodyEn
    }
}