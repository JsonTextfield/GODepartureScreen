package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jsontextfield.departurescreen.entities.Alert
import com.jsontextfield.departurescreen.entities.CombinedStation
import com.jsontextfield.departurescreen.ui.menu.Action
import com.jsontextfield.departurescreen.ui.menu.getActions
import com.jsontextfield.departurescreen.ui.theme.MyApplicationTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(mainViewModel: MainViewModel = koinViewModel()) {
    val uiState by mainViewModel.uiState.collectAsState()
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()
    val informationAlerts by mainViewModel.informationAlerts.collectAsState()
    val serviceAlerts by mainViewModel.serviceAlerts.collectAsState()
    App(
        uiState = uiState,
        showAlerts = mainViewModel.showAlerts,
        showStationMenu = mainViewModel.showStationMenu,
        informationAlerts = informationAlerts,
        serviceAlerts = serviceAlerts,
        timeRemaining = timeRemaining,
        actions = getActions(mainViewModel),
        onRetryClicked = mainViewModel::loadData,
        onRefresh = mainViewModel::refresh,
        onSetVisibleTrains = mainViewModel::setVisibleTrains,
        onBackPressed = mainViewModel::onBackPressed,
        onShowStationMenu = mainViewModel::showStationMenu,
        onStationSelected = mainViewModel::setSelectedStation,
        onRefreshAlerts = mainViewModel::loadAlerts,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    uiState: UIState,
    showAlerts: Boolean = false,
    showStationMenu: Boolean = false,
    informationAlerts: List<Alert> = emptyList(),
    serviceAlerts: List<Alert> = emptyList(),
    timeRemaining: Int,
    actions: List<Action>,
    onRetryClicked: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onRefreshAlerts: () -> Unit = {},
    onSetVisibleTrains: (Set<String>) -> Unit = {},
    onShowStationMenu: () -> Unit = {},
    onStationSelected: (CombinedStation) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    MyApplicationTheme(theme = uiState.theme) {
        MainScreen(
            uiState = uiState,
            timeRemaining = timeRemaining,
            actions = actions,
            onRetryClicked = onRetryClicked,
            onRefresh = onRefresh,
            onSetVisibleTrains = onSetVisibleTrains,
            onShowStationMenu = onShowStationMenu,
        )
        AnimatedVisibility(
            visible = showAlerts,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it },
        ) {
            AlertScreen(
                informationAlerts = informationAlerts,
                serviceAlerts = serviceAlerts,
                onBackPressed = onBackPressed,
                isRefreshing = uiState.isAlertsRefreshing,
                onRefresh = onRefreshAlerts,
            )
        }
        AnimatedVisibility(
            visible = showStationMenu,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it },
        ) {
            StationsScreen(
                uiState = uiState,
                onStationSelected = onStationSelected,
                onBackPressed = onBackPressed
            )
        }
    }
}