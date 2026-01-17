package com.jsontextfield.departurescreen.ui.menu

import OverflowMenu
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.ThemeMode
import com.jsontextfield.departurescreen.core.ui.navigation.NavigationActions
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.add_widget
import departure_screen.composeapp.generated.resources.alerts
import departure_screen.composeapp.generated.resources.favourite
import departure_screen.composeapp.generated.resources.more
import departure_screen.composeapp.generated.resources.rate_app
import departure_screen.composeapp.generated.resources.round_bus_alert_24
import departure_screen.composeapp.generated.resources.round_rate_review_24
import departure_screen.composeapp.generated.resources.round_star_24
import departure_screen.composeapp.generated.resources.round_star_border_24
import departure_screen.composeapp.generated.resources.rounded_add_24
import departure_screen.composeapp.generated.resources.rounded_brightness_4_24
import departure_screen.composeapp.generated.resources.rounded_more_vert_24
import departure_screen.composeapp.generated.resources.rounded_sort_24
import departure_screen.composeapp.generated.resources.sort
import departure_screen.composeapp.generated.resources.theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActionBar(
    maxActions: Int,
    actions: List<Action>,
) {
    var openMenuIndex by remember { mutableStateOf(-1) }

    val displayActions = actions.take(maxActions)
    val overflowActions = actions.drop(maxActions)

    displayActions.forEachIndexed { index, action ->
        Box {
            MenuItem(
                icon = action.icon,
                tooltip = action.tooltip,
                onClick = {
                    if (action.menuContent != null) {
                        openMenuIndex = index
                    } else {
                        action.onClick?.invoke()
                    }
                },
                isAlertIndicatorVisible = action.isAlertIndicatorVisible,
            )

            if (action.menuContent != null && openMenuIndex == index) {
                PopupMenu(
                    showMenu = true,
                    onDismissRequest = { openMenuIndex = -1 },
                    menuContent = {
                        action.menuContent { openMenuIndex = -1 }
                    },
                )
            }
        }
    }

    if (overflowActions.isNotEmpty()) {
        Box {
            var showOverflowMenu by remember { mutableStateOf(false) }

            MenuItem(
                icon = Res.drawable.rounded_more_vert_24,
                tooltip = stringResource(Res.string.more),
                onClick = { showOverflowMenu = true },
                isAlertIndicatorVisible = overflowActions.any { it.isAlertIndicatorVisible }
            )

            OverflowMenu(
                isExpanded = showOverflowMenu,
                actions = overflowActions,
                onDismissRequest = { showOverflowMenu = false },
            )
        }
    }
}

expect fun addWidgetAction()

expect fun rateAppAction()

expect fun isAddWidgetActionVisible() : Boolean

expect fun isRateAppActionVisible() : Boolean

@Composable
fun getActions(
    mainViewModel: MainViewModel,
    navigationActions: NavigationActions,
): List<Action> {
    val uiState by mainViewModel.uiState.collectAsState()
    val favourite = Action(
        icon = if (uiState.selectedStop?.isFavourite == true) {
            Res.drawable.round_star_24
        } else {
            Res.drawable.round_star_border_24
        },
        tooltip = stringResource(Res.string.favourite),
        isVisible = uiState.selectedStop != null,
        onClick = {
            uiState.selectedStop?.let(mainViewModel::setFavouriteStops)
        },
    )

    val sort = Action(
        icon = Res.drawable.rounded_sort_24,
        tooltip = stringResource(Res.string.sort),
        isVisible = uiState.allTrips.isNotEmpty(),
        menuContent = { onDismiss ->
            for (sortMode in SortMode.entries) {
                RadioMenuItem(
                    title = stringResource(sortMode.key),
                    isSelected = uiState.sortMode == sortMode,
                    onClick = {
                        mainViewModel.setSortMode(sortMode)
                        onDismiss()
                    },
                )
            }
        },
    )

    val theme = Action(
        icon = Res.drawable.rounded_brightness_4_24,
        tooltip = stringResource(Res.string.theme),
        menuContent = { onDismiss ->
            for (themeMode in ThemeMode.entries) {
                RadioMenuItem(
                    title = stringResource(themeMode.key),
                    isSelected = uiState.theme == themeMode,
                    onClick = {
                        mainViewModel.setTheme(themeMode)
                        onDismiss()
                    },
                )
            }
        },
    )

    val alerts = Action(
        icon = Res.drawable.round_bus_alert_24,
        tooltip = stringResource(Res.string.alerts),
        onClick = navigationActions.onShowAlerts,
        isAlertIndicatorVisible = uiState.unreadAlertsCount > 0,
    )

    val addWidget = Action(
        icon = Res.drawable.rounded_add_24,
        tooltip = stringResource(Res.string.add_widget),
        onClick = { addWidgetAction() },
        menuContent = null,
        isVisible = isAddWidgetActionVisible(),
    )

    val rate = Action(
        icon = Res.drawable.round_rate_review_24,
        tooltip = stringResource(Res.string.rate_app),
        onClick = { rateAppAction() },
        menuContent = null,
        isVisible = isRateAppActionVisible(),
    )

    return listOf(
        favourite,
        sort,
        alerts,
        theme,
        rate,
        addWidget,
    ).filter { it.isVisible }
}