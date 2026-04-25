package com.jsontextfield.departurescreen.ui.menu

import OverflowMenu
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainUIState
import com.jsontextfield.departurescreen.ui.intents.ActionBarAction
import com.jsontextfield.departurescreen.ui.intents.Alerts
import com.jsontextfield.departurescreen.ui.intents.SetFavouriteStop
import com.jsontextfield.departurescreen.ui.intents.SetSortMode
import com.jsontextfield.departurescreen.ui.intents.Settings
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.add_widget
import departure_screen.composeapp.generated.resources.alerts
import departure_screen.composeapp.generated.resources.favourite
import departure_screen.composeapp.generated.resources.more
import departure_screen.composeapp.generated.resources.rate_app
import departure_screen.composeapp.generated.resources.round_bus_alert_24
import departure_screen.composeapp.generated.resources.round_rate_review_24
import departure_screen.composeapp.generated.resources.round_settings_24
import departure_screen.composeapp.generated.resources.round_star_24
import departure_screen.composeapp.generated.resources.round_star_border_24
import departure_screen.composeapp.generated.resources.rounded_add_24
import departure_screen.composeapp.generated.resources.rounded_more_vert_24
import departure_screen.composeapp.generated.resources.rounded_sort_24
import departure_screen.composeapp.generated.resources.settings
import departure_screen.composeapp.generated.resources.sort
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActionBar(
    uiState: MainUIState,
    onActionClicked: (ActionBarAction) -> Unit,
) {
    var openMenuIndex by remember { mutableStateOf(-1) }

    val actions = getActions(
        uiState = uiState,
        onAction = onActionClicked,
    )

    val maxActions = with(LocalDensity.current) {
        val screenWidthDp = LocalWindowInfo.current.containerSize.width.toDp()
        val fraction = when {
            screenWidthDp.value < 400 -> 1 / 4f
            screenWidthDp.value < 600 -> 1 / 3f
            screenWidthDp.value < 800 -> 1 / 2f
            else -> 2 / 3f
        }
        val max = (screenWidthDp.value * fraction / 48f).toInt()
        if (actions.size - max == 1) {
            max + 1
        } else {
            max
        }
    }

    val (displayActions, overflowActions) = actions.partition { actions.indexOf(it) < maxActions }

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

expect fun isAddWidgetActionVisible(): Boolean

expect fun isRateAppActionVisible(): Boolean

@Composable
fun getActions(
    uiState: MainUIState,
    onAction: (ActionBarAction) -> Unit,
): List<Action> {
    val favourite = Action(
        icon = if (uiState.selectedStop?.isFavourite == true) {
            Res.drawable.round_star_24
        } else {
            Res.drawable.round_star_border_24
        },
        tooltip = stringResource(Res.string.favourite),
        isVisible = uiState.selectedStop != null,
        onClick = {
            uiState.selectedStop?.let { onAction(SetFavouriteStop(it)) }
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
                        onAction(SetSortMode(sortMode))
                        onDismiss()
                    },
                )
            }
        },
    )

    val alerts = Action(
        icon = Res.drawable.round_bus_alert_24,
        tooltip = stringResource(Res.string.alerts),
        onClick = { onAction(Alerts) },
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

    val settings = Action(
        icon = Res.drawable.round_settings_24,
        tooltip = stringResource(Res.string.settings),
        onClick = { onAction(Settings) },
    )

    return listOf(
        favourite,
        sort,
        alerts,
        rate,
        addWidget,
        settings,
    ).filter { it.isVisible }
}