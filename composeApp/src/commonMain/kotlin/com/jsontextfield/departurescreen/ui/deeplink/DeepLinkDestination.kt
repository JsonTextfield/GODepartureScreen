package com.jsontextfield.departurescreen.ui.deeplink

import io.ktor.http.parseUrl

sealed class DeepLinkDestination {
    data class Stop(val stopName: String) : DeepLinkDestination()
    data class Trip(
        val tripId: String,
        val stopName: String,
        val stopCode: String,
        val code: String,
        val destination: String
    ) : DeepLinkDestination()
}

object DeepLinkParser {
    fun parse(url: String): DeepLinkDestination? {
        return parseUrl(url)?.let { parsedUrl ->
            //println(parsedUrl)
            if (parsedUrl.encodedPath.contains("/trips/")) {
                DeepLinkDestination.Trip(
                    parsedUrl.encodedPath.split("/").last(),
                    parsedUrl.parameters.get("stopName").orEmpty(),
                    parsedUrl.parameters.get("stopCode").orEmpty(),
                    parsedUrl.parameters.get("lineCode").orEmpty(),
                    parsedUrl.parameters.get("destination").orEmpty(),
                )
            } else if (parsedUrl.encodedPath.contains("/stops/")) {
                DeepLinkDestination.Stop(parsedUrl.segments.last())
            } else {
                null
            }
        }
    }
}