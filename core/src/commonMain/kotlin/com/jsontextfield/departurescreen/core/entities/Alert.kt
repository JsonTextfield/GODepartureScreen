package com.jsontextfield.departurescreen.core.entities

import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.Instant

data class Alert(
    val id: String,
    val date: Instant,
    val affectedLines: List<String> = emptyList(),
    val affectedStations: List<String> = emptyList(),
    private val subjectEn: String = "",
    private val subjectFr: String = "",
    private val bodyEn: String = "",
    private val bodyFr: String = "",
) {
    val subject: String
        get() = if ("en" in Locale.current.language) subjectEn else subjectFr

    val body: String
        get() = if ("en" in Locale.current.language) bodyEn else bodyFr
}