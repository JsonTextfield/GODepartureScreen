package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jsontextfield.departurescreen.entities.Alert
import com.jsontextfield.departurescreen.entities.Station
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
        informationAlerts = informationAlerts,
        serviceAlerts = serviceAlerts,
        timeRemaining = timeRemaining,
        actions = getActions(mainViewModel),
        onRetryClicked = mainViewModel::loadData,
        onSetVisibleTrains = mainViewModel::setVisibleTrains,
        onBackPressed = mainViewModel::showAlertsScreen,
        onStationSelected = mainViewModel::setSelectedStation
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    uiState: UIState,
    showAlerts: Boolean = false,
    informationAlerts: List<Alert> = emptyList(),
    serviceAlerts: List<Alert> = emptyList(),
    timeRemaining: Int,
    actions: List<Action>,
    onRetryClicked: () -> Unit,
    onSetVisibleTrains: (Set<String>) -> Unit,
    onStationSelected: (Station) -> Unit,
    onBackPressed: () -> Unit = {},
) {
    MyApplicationTheme(theme = uiState.theme) {
        MainScreen(
            uiState = uiState,
            timeRemaining = timeRemaining,
            actions = actions,
            onRetryClicked = onRetryClicked,
            onSetVisibleTrains = onSetVisibleTrains,
            onStationSelected = onStationSelected,
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
            )
        }
    }
}