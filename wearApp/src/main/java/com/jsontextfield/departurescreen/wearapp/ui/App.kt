package com.jsontextfield.departurescreen.wearapp.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.jsontextfield.departurescreen.core.ui.navigation.AlertsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.HomeRoute
import com.jsontextfield.departurescreen.core.ui.navigation.NavigationActions
import com.jsontextfield.departurescreen.core.ui.navigation.StationsRoute
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.StationsViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

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
    AppTheme(uiState.theme) {
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
        ) {
            composable<HomeRoute> {
                MainScreen(
                    mainViewModel = mainViewModel,
                    navigationActions = NavigationActions(
                        onShowAlerts = {
                            navController.navigate(AlertsRoute) {
                                launchSingleTop = true
                            }
                        },
                        onShowStations = {
                            navController.navigate(StationsRoute) {
                                launchSingleTop = true
                            }
                        },
                    )
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

            composable<StationsRoute>(
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { it } },
            ) {
                val stationsViewModel = koinViewModel<StationsViewModel>()
                StationsScreen(
                    stationsViewModel = stationsViewModel,
                    onBackPressed = {
                        safeNavigation { navController.popBackStack() }
                    },
                )
            }

        }
    }
}