package com.jsontextfield.departurescreen.core.entities

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
    val isRead: Boolean = false,
) {
    fun getSubject(language: String): String {
        return if ("fr" in language) subjectFr else subjectEn
    }

    fun getBody(language: String): String {
        return if ("fr" in language) bodyFr else bodyEn
    }
}