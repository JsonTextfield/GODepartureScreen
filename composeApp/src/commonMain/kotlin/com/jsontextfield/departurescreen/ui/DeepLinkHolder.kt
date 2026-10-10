package com.jsontextfield.departurescreen.ui

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.BackStackMatchResult
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.withBackStack
import com.jsontextfield.departurescreen.core.ui.navigation.AlertDetailsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.AlertsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.HomeRoute
import com.jsontextfield.departurescreen.core.ui.navigation.SettingsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.StopsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.TripDetailsRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.serializer

object DeepLinkHolder {
    private val _pendingUrl = MutableStateFlow<String?>(null)
    val pendingUrl: StateFlow<String?> = _pendingUrl.asStateFlow()

    fun handle(url: String) {
        _pendingUrl.value = url
    }

    fun consume() {
        _pendingUrl.value = null
    }

    private val matchers: List<DeepLinkMatcher<NavKey, *>> = listOf(
        UriDeepLinkMatcher(
            DeepLinkUri("go-departures.app/alerts/{alertId}"),
            serializer<AlertDetailsRoute>()
        ).withBackStack { matchResult ->
            listOf(HomeRoute(), AlertsRoute, matchResult.key)
        },
        UriDeepLinkMatcher(
            DeepLinkUri("go-departures.app/alerts?alertId={alertId}"),
            serializer<AlertDetailsRoute>()
        ).withBackStack { matchResult ->
            listOf(HomeRoute(), AlertsRoute, matchResult.key)
        },
        UriDeepLinkMatcher(
            DeepLinkUri("go-departures.app/alerts"),
            serializer<AlertsRoute>()
        ).withBackStack { matchResult ->
            listOf(HomeRoute(), matchResult.key)
        },
        UriDeepLinkMatcher(
            DeepLinkUri("go-departures.app/trips?stopName={stopName}&stopCode={stopCode}&tripId={tripId}&lineCode={lineCode}&destination={destination}"),
            serializer<TripDetailsRoute>()
        ).withBackStack { matchResult ->
            listOf(HomeRoute(matchResult.key.stopName), matchResult.key)
        },
        UriDeepLinkMatcher(
            DeepLinkUri("go-departures.app/stops?selectedStopName={selectedStopName}"),
            serializer<StopsRoute>()
        ).withBackStack { matchResult ->
            listOf(HomeRoute(), matchResult.key)
        },
        UriDeepLinkMatcher(
            DeepLinkUri("go-departures.app/settings"),
            serializer<SettingsRoute>()
        ).withBackStack { matchResult ->
            listOf(HomeRoute(), matchResult.key)
        },
        UriDeepLinkMatcher(
            DeepLinkUri("go-departures.app/?selectedStop={selectedStop}"),
            serializer<HomeRoute>()
        ).withBackStack { matchResult ->
            listOf(matchResult.key)
        }
    )

    fun parseDeepLink(urlStr: String): List<NavKey> {
        val cleanUrl = if (!urlStr.contains("://")) "https://$urlStr" else urlStr
        val request = DeepLinkRequest(cleanUrl)
        val bestMatch = matchers.mapNotNull { it.match(request) }.maxOrNull()
        return when (bestMatch) {
            is BackStackMatchResult<*, *> -> bestMatch.backStack.filterIsInstance<NavKey>()
            else -> listOf(HomeRoute())
        }
    }
}