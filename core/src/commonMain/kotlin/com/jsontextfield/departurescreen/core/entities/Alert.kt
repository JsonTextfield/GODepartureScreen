@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
    val bodyEn: AnnotatedString = AnnotatedString(""),
    val bodyFr: AnnotatedString = AnnotatedString(""),
    val isRead: Boolean = false,
    val urlEn: String? = null,
    val urlFr: String? = null,
) {
    @OptIn(FormatStringsInDatetimeFormats::class)
    val dateDifference: Duration =
        Clock.System.now().toLocalDateTime(TimeZone.of("America/Toronto")).toInstant(TimeZone.UTC) - date

    fun getSubject(language: String): String {
        return subjectFr.takeIf { "fr" in language && it.isNotEmpty() } ?: subjectEn
    }

    fun getBody(language: String): AnnotatedString {
        return bodyFr.takeIf { "fr" in language && it.isNotEmpty() } ?: bodyEn
    }

    fun getAnnotatedBody(language: String): AnnotatedString {
        return getBody(language)
    }

    companion object {
        fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
            return buildAnnotatedString {
                // Remove style tags and their content
                val sanitizedHtml = html.replace(Regex("(?s)<style[^>]*>.*?</style>"), "")
                    .replace(Regex("(?s)<!--.*?-->"), "")
                
                var currentPos = 0
                val tagStack = mutableListOf<String>()

                // Simple regex to find HTML tags
                val tagRegex = Regex("<(/?[a-zA-Z0-9:]+)([^>]*)>")
                val matches = tagRegex.findAll(sanitizedHtml)

                for (match in matches) {
                    // Append text before the tag
                    val textBefore = sanitizedHtml.substring(currentPos, match.range.first)
                    append(decodeHtmlEntities(textBefore))

                    val fullTag = match.groupValues[0]
                    val tagName = match.groupValues[1].lowercase()
                    val attributes = match.groupValues[2]

                    if (tagName.startsWith("/")) {
                        // Closing tag
                        val openTag = tagName.substring(1)
                        while (tagStack.isNotEmpty() && tagStack.last() != openTag) {
                            try {
                                pop()
                            } catch (_: Exception) {
                            }
                            tagStack.removeAt(tagStack.size - 1)
                        }
                        if (tagStack.isNotEmpty()) {
                            try {
                                pop()
                                if (openTag == "a") {
                                    pop() // Pop the extra style for <a>
                                }
                            } catch (_: Exception) {
                            }
                            tagStack.removeAt(tagStack.size - 1)
                        }
                    } else {
                        // Opening tag
                        when (tagName) {
                            "b", "strong" -> {
                                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                                tagStack.add(tagName)
                            }

                            "i", "em" -> {
                                pushStyle(SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                                tagStack.add(tagName)
                            }

                            "u" -> {
                                pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                                tagStack.add(tagName)
                            }

                            "a" -> {
                                val hrefMatch = Regex("href=\"([^\"]+)\"").find(attributes)
                                val url = hrefMatch?.groupValues?.get(1)
                                if (url != null) {
                                    pushLink(LinkAnnotation.Url(url))
                                    pushStyle(
                                        SpanStyle(
                                            color = Color.Cyan,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    )
                                    tagStack.add(tagName)
                                }
                            }

                            "br" -> {
                                append("\n")
                            }

                            "div", "p" -> {
                                if (this.length > 0 && !this.toString().endsWith("\n")) {
                                    append("\n")
                                }
                                tagStack.add(tagName)
                            }

                            else -> {
                                // Unsupported tag, just track it if it's not self-closing
                                if (!fullTag.endsWith("/>") && tagName != "br") {
                                    tagStack.add(tagName)
                                }
                            }
                        }
                    }
                    currentPos = match.range.last + 1
                }

                // Pop any remaining styles
                while (tagStack.isNotEmpty()) {
                    try {
                        pop()
                    } catch (_: Exception) {
                    }
                    tagStack.removeAt(tagStack.size - 1)
                }

                // Append remaining text
                if (currentPos < sanitizedHtml.length) {
                    append(decodeHtmlEntities(sanitizedHtml.substring(currentPos)))
                }
            }
        }

        private fun decodeHtmlEntities(text: String): String {
            return text.replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&#183;", "·")
                .replace("&#8217;", "'")
                .replace("&#8216;", "'")
                .replace("&#39;", "'")
                .replace("&#8211;", "-")
        }
    }
}
