package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jsontextfield.departurescreen.Alert
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.menu.Action
import com.jsontextfield.departurescreen.ui.menu.getActions
import com.jsontextfield.departurescreen.ui.theme.MyApplicationTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(mainViewModel: MainViewModel = koinViewModel()) {
    val uiState by mainViewModel.uiState.collectAsState()
    val allTrains = uiState.allTrains
    val visibleTrains = uiState.visibleTrains
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()
    val informationAlerts by mainViewModel.informationAlerts.collectAsState()
    val serviceAlerts by mainViewModel.serviceAlerts.collectAsState()
    App(
        themeMode = uiState.theme,
        showAlerts = mainViewModel.showAlerts,
        informationAlerts = informationAlerts,
        serviceAlerts = serviceAlerts,
        allTrains = allTrains,
        visibleTrains = visibleTrains,
        timeRemaining = timeRemaining,
        actions = getActions(mainViewModel),
        onSetVisibleTrains = mainViewModel::setVisibleTrains,
        onBackPressed = mainViewModel::showAlertsScreen,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    themeMode: ThemeMode = ThemeMode.DEFAULT,
    showAlerts: Boolean = false,
    informationAlerts: List<Alert> = emptyList(),
    serviceAlerts: List<Alert> = emptyList(),
    allTrains: List<Train>,
    visibleTrains: Set<String>,
    timeRemaining: Int,
    actions: List<Action>,
    onSetVisibleTrains: (Set<String>) -> Unit,
    onBackPressed: () -> Unit = {},
) {
    MyApplicationTheme(theme = themeMode) {
        MainScreen(
            allTrains = allTrains,
            visibleTrains = visibleTrains,
            timeRemaining = timeRemaining,
            actions = actions,
            onSetVisibleTrains = onSetVisibleTrains,
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