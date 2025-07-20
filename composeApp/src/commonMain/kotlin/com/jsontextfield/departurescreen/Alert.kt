package com.jsontextfield.departurescreen

import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.Instant

data class Alert(
    val id: String,
    val date: Instant,
    val subjectEn: String = "",
    val subjectFr: String = "",
    val bodyEn: String = "",
    val bodyFr: String = "",
) {
    val subject: String
        get() = if ("en" in Locale.current.language) subjectEn else subjectFr

    val body: String
        get() = if ("en" in Locale.current.language) bodyEn else bodyFr
}