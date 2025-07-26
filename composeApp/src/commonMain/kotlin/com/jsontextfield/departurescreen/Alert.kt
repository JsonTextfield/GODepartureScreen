package com.jsontextfield.departurescreen

import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.Instant

data class Alert(
    val id: String,
    val date: Instant,
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