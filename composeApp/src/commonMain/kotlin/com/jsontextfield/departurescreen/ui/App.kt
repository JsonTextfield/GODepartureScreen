package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.jsontextfield.departurescreen.core.ui.navigation.AlertDetailsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.AlertsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.HomeRoute
import com.jsontextfield.departurescreen.core.ui.navigation.SettingsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.StopsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.TripDetailsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.coreNavConfig
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertDetailsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.TripDetailsViewModel
import com.jsontextfield.departurescreen.ui.intents.Alerts
import com.jsontextfield.departurescreen.ui.intents.Settings
import com.jsontextfield.departurescreen.ui.intents.Stops
import com.jsontextfield.departurescreen.ui.intents.TripDetails
import com.jsontextfield.departurescreen.ui.views.AlertDetailsScreen
import com.jsontextfield.departurescreen.ui.views.AlertsScreen
import com.jsontextfield.departurescreen.ui.views.MainScreen
import com.jsontextfield.departurescreen.ui.views.SettingsScreen
import com.jsontextfield.departurescreen.ui.views.StopsScreen
import com.jsontextfield.departurescreen.ui.views.TripDetailsScreen
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

const val BASE_URL = "go-departures.app"
const val TRIPS_URL = "$BASE_URL/trips"
const val ALERTS_URL = "$BASE_URL/alerts"


@Composable
fun App(
    mainViewModel: MainViewModel = koinViewModel<MainViewModel>(),
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val pendingUrl by DeepLinkHolder.pendingUrl.collectAsState()

    val initialKeys = remember {
        val url = DeepLinkHolder.pendingUrl.value
        val keys = url?.let { DeepLinkHolder.parseDeepLink(url) } ?: listOf(HomeRoute())
        if (url != null) {
            DeepLinkHolder.consume()
        }
        keys
    }

    val backStack = rememberNavBackStack(coreNavConfig, *initialKeys.toTypedArray())
    var isNavigating by remember { mutableStateOf(false) }

    fun safeNavigation(navigation: () -> Unit) {
        if (!isNavigating) {
            isNavigating = true
            navigation()
        }
    }

    LaunchedEffect(pendingUrl) {
        pendingUrl?.let { url ->
            val keys = DeepLinkHolder.parseDeepLink(url)
            if (keys.isNotEmpty()) {
                val lastKey = keys.last()
                if (lastKey is HomeRoute) {
                    lastKey.selectedStop?.let(mainViewModel::setSelectedStop)
                }
                backStack.clear()
                backStack.addAll(keys)
            }
            DeepLinkHolder.consume()
        }
    }

    LaunchedEffect(isNavigating) {
        if (isNavigating) {
            delay(500)
            isNavigating = false
        }
    }

    AppTheme(uiState.theme, uiState.contrast, uiState.useDynamicTheme) {
        Surface {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWideScreen = maxWidth >= 1200.dp
                val isExtraWideScreen = maxWidth >= 1600.dp
                val adaptiveSceneStrategy = remember(isWideScreen, isExtraWideScreen) {
                    TransitAdaptiveSceneStrategy<NavKey>(
                        isWideScreen = isWideScreen,
                        isExtraWideScreen = isExtraWideScreen,
                        onDismissAlert = {
                            safeNavigation {
                                if (backStack.lastOrNull() is AlertDetailsRoute) {
                                    backStack.removeLastOrNull()
                                }
                            }
                        },
                        onDismissStops = {
                            safeNavigation {
                                if (backStack.lastOrNull() is StopsRoute) {
                                    backStack.removeLastOrNull()
                                }
                            }
                        },
                        onDismissSettings = {
                            safeNavigation {
                                if (backStack.lastOrNull() is SettingsRoute) {
                                    backStack.removeLastOrNull()
                                }
                            }
                        }
                    )
                }
                val alertsAdaptiveSceneStrategy = remember(isWideScreen) {
                    AlertsAdaptiveSceneStrategy<NavKey>(isWideScreen = isWideScreen)
                }
                val stopsDrawerSceneStrategy = remember(isWideScreen) {
                    StopsDrawerSceneStrategy<NavKey>(
                        isWideScreen = isWideScreen,
                        onDismissStops = {
                            safeNavigation {
                                if (backStack.lastOrNull() is StopsRoute) {
                                    backStack.removeLastOrNull()
                                }
                            }
                        }
                    )
                }
                val settingsDrawerSceneStrategy = remember(isWideScreen) {
                    SettingsDrawerSceneStrategy<NavKey>(
                        isWideScreen = isWideScreen,
                        onDismissSettings = {
                            safeNavigation {
                                if (backStack.lastOrNull() is SettingsRoute) {
                                    backStack.removeLastOrNull()
                                }
                            }
                        }
                    )
                }

                NavDisplay(
                    backStack = backStack,
                    onBack = { safeNavigation { backStack.removeLastOrNull() } },
                    sceneStrategies = listOf(
                        adaptiveSceneStrategy,
                        alertsAdaptiveSceneStrategy,
                        stopsDrawerSceneStrategy,
                        settingsDrawerSceneStrategy
                    ),
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it })
                    },
                    popTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    },
                    predictivePopTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    },
                    entryProvider = entryProvider {
                        entry<HomeRoute>(
                            metadata = TransitAdaptiveScene.homePane()
                        ) { route ->
                            route.selectedStop?.let(mainViewModel::setSelectedStop)
                            MainScreen(
                                mainViewModel = mainViewModel,
                                onNavigationAction = { action ->
                                    when (action) {
                                        Alerts -> {
                                            while (backStack.size > 1 && backStack.lastOrNull() !is HomeRoute) {
                                                backStack.removeLastOrNull()
                                            }
                                            backStack.add(AlertsRoute)
                                        }

                                        Settings -> {
                                            backStack.add(SettingsRoute)
                                        }

                                        is Stops -> {
                                            backStack.add(StopsRoute(action.selectedStopName))
                                        }

                                        is TripDetails -> {
                                            backStack.add(
                                                TripDetailsRoute(
                                                    stopName = action.trip.stopName.orEmpty(),
                                                    stopCode = action.trip.stopCode.orEmpty(),
                                                    tripId = action.trip.id,
                                                    lineCode = action.trip.code,
                                                    destination = action.trip.destination,
                                                )
                                            )
                                        }
                                    }
                                },
                            )
                        }

                        entry<AlertsRoute>(
                            metadata = AlertsAdaptiveScene.alertsPane()
                        ) {
                            val alertsViewModel = koinViewModel<AlertsViewModel>()
                            AlertsScreen(
                                alertsViewModel = alertsViewModel,
                                onBackPressed = {
                                    safeNavigation {
                                        while (backStack.size > 1 && backStack.lastOrNull() !is HomeRoute) {
                                            backStack.removeLastOrNull()
                                        }
                                    }
                                },
                                onAlertClicked = { alertId ->
                                    safeNavigation {
                                        if (backStack.lastOrNull() is AlertDetailsRoute) {
                                            backStack.removeLastOrNull()
                                        }
                                        backStack.add(AlertDetailsRoute(alertId))
                                    }
                                }
                            )
                        }

                        entry<AlertDetailsRoute>(
                            metadata = metadata {
                                put(TransitAdaptiveScene.AlertKey, true)
                                put(AlertsAdaptiveScene.AlertDetailsKey, true)
                            }
                        ) { route ->
                            val alertDetailsViewModel = koinViewModel<AlertDetailsViewModel>(key = route.alertId) {
                                parametersOf(route.alertId)
                            }
                            AlertDetailsScreen(
                                alertDetailsViewModel = alertDetailsViewModel,
                                onBackPressed = {
                                    safeNavigation { backStack.removeLastOrNull() }
                                },
                            )
                        }

                        entry<StopsRoute>(
                            metadata = StopsDrawerScene.stopsPane()
                        ) { route ->
                            val selectedStopCode = route.selectedStopName
                            val stopsViewModel = koinViewModel<StopsViewModel>(key = selectedStopCode.orEmpty()) {
                                parametersOf(selectedStopCode)
                            }
                            StopsScreen(
                                stopsViewModel = stopsViewModel,
                                onStopSelected = { stop ->
                                    safeNavigation {
                                        backStack.clear()
                                        backStack.add(HomeRoute(stop.name))
                                    }
                                },
                                onBackPressed = {
                                    safeNavigation { backStack.removeLastOrNull() }
                                },
                            )
                        }

                        entry<TripDetailsRoute>(
                            metadata = TransitAdaptiveScene.tripPane()
                        ) { route ->
                            val tripDetailsViewModel = koinViewModel<TripDetailsViewModel>(key = route.tripId) {
                                parametersOf(
                                    route.stopName,
                                    route.stopCode,
                                    route.tripId,
                                    route.lineCode,
                                    route.destination,
                                )
                            }
                            TripDetailsScreen(
                                tripDetailsViewModel = tripDetailsViewModel,
                                onBackPressed = {
                                    safeNavigation {
                                        while (backStack.size > 1 && backStack.lastOrNull() !is HomeRoute) {
                                            backStack.removeLastOrNull()
                                        }
                                    }
                                },
                                onTripSelected = { trip ->
                                    safeNavigation {
                                        if (backStack.lastOrNull() is TripDetailsRoute) {
                                            backStack.removeLastOrNull()
                                        }
                                        backStack.add(
                                            TripDetailsRoute(
                                                stopName = route.stopName,
                                                stopCode = route.stopCode,
                                                tripId = trip.id,
                                                lineCode = trip.code,
                                                destination = trip.destination,
                                            )
                                        )
                                    }
                                },
                                onAlertClicked = { alertId ->
                                    safeNavigation {
                                        backStack.add(AlertDetailsRoute(alertId))
                                    }
                                },
                                onStopClicked = { stopName ->
                                    safeNavigation {
                                        backStack.add(HomeRoute(stopName))
                                    }
                                }
                            )
                        }

                        entry<SettingsRoute>(
                            metadata = SettingsDrawerScene.settingsPane()
                        ) {
                            SettingsScreen(onBackPressed = {
                                safeNavigation { backStack.removeLastOrNull() }
                            })
                        }
                    }
                )
            }
        }
    }
}