package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.StationsViewModel
import com.jsontextfield.departurescreen.ui.navigation.AlertsRoute
import com.jsontextfield.departurescreen.ui.navigation.HomeRoute
import com.jsontextfield.departurescreen.ui.navigation.NavigationActions
import com.jsontextfield.departurescreen.ui.navigation.StationsRoute
import com.jsontextfield.departurescreen.ui.views.AlertsScreen
import com.jsontextfield.departurescreen.ui.views.MainScreen
import com.jsontextfield.departurescreen.ui.views.StationsScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(mainViewModel: MainViewModel = koinViewModel<MainViewModel>()) {
    val uiState by mainViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    AppTheme(uiState.theme) {
        Surface {
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
                            navController.navigate(HomeRoute) {
                                launchSingleTop = true
                            }
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
                            navController.navigate(HomeRoute) {
                                launchSingleTop = true
                            }
                        },
                    )
                }

            }
        }
    }
}