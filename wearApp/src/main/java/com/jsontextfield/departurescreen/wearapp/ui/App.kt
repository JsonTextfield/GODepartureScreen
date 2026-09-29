package com.jsontextfield.departurescreen.wearapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.jsontextfield.departurescreen.core.ui.navigation.AlertsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.HomeRoute
import com.jsontextfield.departurescreen.core.ui.navigation.NavigationActions
import com.jsontextfield.departurescreen.core.ui.navigation.StopsRoute
import com.jsontextfield.departurescreen.core.ui.navigation.coreNavConfig
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(mainViewModel: MainViewModel = koinViewModel<MainViewModel>()) {
    val uiState by mainViewModel.uiState.collectAsState()
    val backStack = rememberNavBackStack(coreNavConfig, HomeRoute())
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
        NavDisplay(
            backStack = backStack,
            onBack = { safeNavigation { backStack.removeLastOrNull() } },
            entryProvider = entryProvider {
                entry<HomeRoute> {
                    MainScreen(
                        mainViewModel = mainViewModel,
                        navigationActions = NavigationActions(
                            onShowAlerts = {
                                if (backStack.lastOrNull() != AlertsRoute) {
                                    backStack.add(AlertsRoute)
                                }
                            },
                            onShowStops = {
                                if (backStack.lastOrNull() !is StopsRoute) {
                                    backStack.add(StopsRoute())
                                }
                            },
                        )
                    )
                }

                entry<AlertsRoute> {
                    val alertsViewModel = koinViewModel<AlertsViewModel>()
                    AlertsScreen(
                        alertsViewModel = alertsViewModel,
                        onBackPressed = {
                            safeNavigation { backStack.removeLastOrNull() }
                        },
                    )
                }

                entry<StopsRoute> {
                    val stopsViewModel = koinViewModel<StopsViewModel>()
                    StopsScreen(
                        stopsViewModel = stopsViewModel,
                        onBackPressed = {
                            safeNavigation { backStack.removeLastOrNull() }
                        },
                    )
                }
            }
        )
    }
}