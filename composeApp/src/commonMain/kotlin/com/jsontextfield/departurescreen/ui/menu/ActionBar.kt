package com.jsontextfield.departurescreen.ui.menu

import OverflowMenu
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
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
import departure_screen.composeapp.generated.resources.alerts
import departure_screen.composeapp.generated.resources.favourite
import departure_screen.composeapp.generated.resources.more
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
                }
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
                icon = Icons.Rounded.MoreVert,
                tooltip = stringResource(Res.string.more),
                onClick = { showOverflowMenu = true }
            )

            OverflowMenu(
                isExpanded = showOverflowMenu,
                actions = overflowActions,
                onDismissRequest = { showOverflowMenu = false },
            )
        }
    }
}

@Composable
fun getActions(
    mainViewModel: MainViewModel,
    navigationActions: NavigationActions,
): List<Action> {
    val uiState by mainViewModel.uiState.collectAsState()
    val favourite = Action(
        icon = if (uiState.selectedStation?.isFavourite == true) {
            Icons.Rounded.Star
        } else {
            Icons.Rounded.StarBorder
        },
        tooltip = stringResource(Res.string.favourite),
        isVisible = uiState.selectedStation != null,
        onClick = {
            uiState.selectedStation?.let(mainViewModel::setFavouriteStations)
        },
    )

    val sort = Action(
        icon = Icons.AutoMirrored.Rounded.Sort,
        tooltip = stringResource(Res.string.sort),
        isVisible = uiState.allTrips.isNotEmpty(),
        menuContent = { onDismiss ->
            SortMode.entries.forEach { sortMode ->
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
        icon = Icons.Rounded.BrightnessMedium,
        tooltip = stringResource(Res.string.theme),
        menuContent = { onDismiss ->
            ThemeMode.entries.forEach { themeMode ->
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
        icon = Icons.Rounded.Notifications,
        tooltip = stringResource(Res.string.alerts),
        onClick = navigationActions.onShowAlerts,
    )

    return listOf(
        favourite,
        sort,
        alerts,
        theme,
    ).filter { it.isVisible }
}