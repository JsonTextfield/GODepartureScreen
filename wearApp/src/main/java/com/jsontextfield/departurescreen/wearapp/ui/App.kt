package com.jsontextfield.departurescreen.wearapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.ui.UIState
import com.jsontextfield.departurescreen.core.ui.theme.AppTheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
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
        onRetryClicked = mainViewModel::loadData,
        onRefresh = mainViewModel::refresh,
        onSetVisibleTrains = mainViewModel::setVisibleTrains,
        onBackPressed = mainViewModel::onBackPressed,
        onShowStationMenu = mainViewModel::showStationMenu,
        onStationSelected = mainViewModel::setSelectedStation,
        onFavouriteClick = mainViewModel::setFavouriteStations,
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
    onRetryClicked: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onRefreshAlerts: () -> Unit = {},
    onSetVisibleTrains: (Set<String>) -> Unit = {},
    onShowStationMenu: () -> Unit = {},
    onStationSelected: (CombinedStation) -> Unit = {},
    onFavouriteClick: (CombinedStation) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    AppTheme(theme = uiState.theme) {
        MainScreen(
            uiState = uiState,
            timeRemaining = timeRemaining,
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
                onFavouriteClick = onFavouriteClick,
                onBackPressed = onBackPressed,
            )
        }
    }
}