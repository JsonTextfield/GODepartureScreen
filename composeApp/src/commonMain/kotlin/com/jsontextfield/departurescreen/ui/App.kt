package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.jsontextfield.departurescreen.core.ui.navigation.AlertsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.HomeRoute
import com.jsontextfield.departurescreen.core.ui.navigation.SettingsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.StopsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.TripDetailsRoute
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.TripDetailsViewModel
import com.jsontextfield.departurescreen.ui.intents.Alerts
import com.jsontextfield.departurescreen.ui.intents.Settings
import com.jsontextfield.departurescreen.ui.intents.Stops
import com.jsontextfield.departurescreen.ui.intents.TripDetails
import com.jsontextfield.departurescreen.ui.views.AlertsScreen
import com.jsontextfield.departurescreen.ui.views.MainScreen
import com.jsontextfield.departurescreen.ui.views.SettingsScreen
import com.jsontextfield.departurescreen.ui.views.StopsScreen
import com.jsontextfield.departurescreen.ui.views.TripDetailsScreen
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App(mainViewModel: MainViewModel = koinViewModel<MainViewModel>()) {
    val uiState by mainViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    var isNavigating by remember { mutableStateOf(false) }

    fun safeNavigation(navigation: () -> Unit) {
        if (!isNavigating) {
            isNavigating = true
            navigation()
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
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
            ) {
                composable<HomeRoute> {
                    MainScreen(
                        mainViewModel = mainViewModel,
                        onNavigationAction = { action ->
                            when (action) {
                                Alerts -> {
                                    navController.navigate(AlertsRoute) {
                                        launchSingleTop = true
                                    }
                                }
                                Settings -> {
                                    navController.navigate(SettingsRoute) {
                                        launchSingleTop = true
                                    }
                                }
                                is Stops -> {
                                    navController.navigate(StopsRoute(action.selectedStopCode)) {
                                        launchSingleTop = true
                                    }
                                }
                                is TripDetails -> {
                                    navController.navigate(
                                        TripDetailsRoute(
                                            selectedStop = uiState.selectedStop?.name.orEmpty(),
                                            stopCode = uiState.selectedStop?.code.orEmpty(),
                                            tripId = action.trip.id,
                                            lineCode = action.trip.code,
                                            destination = action.trip.destination,
                                        )
                                    ) {
                                        launchSingleTop = true
                                    }
                                }
                            }
                        },
                    )
                }

                composable<AlertsRoute>(
                    enterTransition = { slideInHorizontally { it } },
                    exitTransition = { slideOutHorizontally { it } },
                ) {
                    val alertsViewModel = koinViewModel<AlertsViewModel>()
                    AlertsScreen(
                        alertsViewModel = alertsViewModel,
                        onBackPressed = {
                            safeNavigation { navController.popBackStack() }
                        },
                    )
                }

                composable<StopsRoute>(
                    enterTransition = { slideInHorizontally { it } },
                    exitTransition = { slideOutHorizontally { it } },
                ) {
                    val selectedStopCode = it.toRoute<StopsRoute>().selectedStopCode
                    val stopsViewModel = koinViewModel<StopsViewModel> {
                        parametersOf(selectedStopCode)
                    }
                    StopsScreen(
                        stopsViewModel = stopsViewModel,
                        onBackPressed = {
                            safeNavigation { navController.popBackStack() }
                        },
                    )
                }

                composable<TripDetailsRoute>(
                    enterTransition = { slideInHorizontally { it } },
                    exitTransition = { slideOutHorizontally { it } },
                ) {
                    val route = it.toRoute<TripDetailsRoute>()
                    val tripDetailsViewModel = koinViewModel<TripDetailsViewModel> {
                        parametersOf(
                            route.selectedStop,
                            route.stopCode,
                            route.tripId,
                            route.lineCode,
                            route.destination,
                        )
                    }
                    TripDetailsScreen(
                        tripDetailsViewModel = tripDetailsViewModel,
                        onBackPressed = {
                            safeNavigation { navController.popBackStack() }
                        },
                        onTripSelected = { trip ->
                            navController.navigate(
                                TripDetailsRoute(
                                    selectedStop = route.selectedStop,
                                    stopCode = route.stopCode,
                                    tripId = trip.id,
                                    lineCode = trip.code,
                                    destination = trip.destination,
                                )
                            ) {
                                popUpTo<TripDetailsRoute> { inclusive = true }
                            }
                        }
                    )
                }

                composable<SettingsRoute>(
                    enterTransition = { slideInHorizontally { it } },
                    exitTransition = { slideOutHorizontally { it } },
                ) {
                    SettingsScreen(onBackPressed = {
                        safeNavigation { navController.popBackStack() }
                    })
                }
            }
        }
    }
}