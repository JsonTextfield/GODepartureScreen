package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jsontextfield.departurescreen.ui.MainViewModel
import com.jsontextfield.departurescreen.ui.SortMode
import com.jsontextfield.departurescreen.ui.ThemeMode
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.more
import departure_screen.composeapp.generated.resources.sort
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActionBar(
    maxActions: Int,
    actions: List<Action>,
) {
    val visibleActions = actions.filter { it.isVisible }
    val displayActions = visibleActions.take(maxActions)
    displayActions.forEach { action ->
        var showMenu by remember { mutableStateOf(false) }
        if (action.menuContent != null) {
            PopupMenu(
                showMenu = showMenu,
                menuContent = action.menuContent,
            )
        }
        MenuItem(
            icon = action.icon,
            tooltip = action.tooltip,
            onClick = {
                if (action.menuContent != null) {
                    showMenu = !showMenu
                } else {
                    action.onClick()
                }
            }
        )
    }

    val overflowActions = visibleActions.drop(maxActions)
    if (overflowActions.isNotEmpty()) {
        Box {
            var showOverflowMenu by remember { mutableStateOf(false) }
            OverflowMenu(
                isExpanded = showOverflowMenu,
                actions = overflowActions,
                onItemSelected = {
                    showOverflowMenu = false
                },
            )
            MenuItem(
                icon = Icons.Rounded.MoreVert,
                tooltip = stringResource(Res.string.more),
                onClick = {
                    showOverflowMenu = true
                }
            )
        }
    }
}

@Composable
fun getActions(
    mainViewModel: MainViewModel,
): List<Action> {
    val sort = Action(
        icon = Icons.AutoMirrored.Rounded.Sort,
        tooltip = stringResource(Res.string.sort),
        isVisible = mainViewModel.uiState.value.allTrains.isNotEmpty(),
        menuContent = {
            var isExpanded by remember { mutableStateOf(it) }
            DropdownMenu(
                expanded = isExpanded xor it,
                onDismissRequest = { isExpanded = !isExpanded },
            ) {
                SortMode.entries.forEach { sortMode ->
                    RadioMenuItem(
                        title = stringResource(sortMode.key),
                        isSelected = mainViewModel.uiState.value.sortMode == sortMode,
                        onClick = {
                            isExpanded = !isExpanded
                            mainViewModel.setSortMode(sortMode)
                        },
                    )
                }
            }
        },
    )

    val theme = Action(
        icon = Icons.Rounded.BrightnessMedium,
        tooltip = "Theme",
        isVisible = true,
        menuContent = {
            var isExpanded by remember { mutableStateOf(it) }
            DropdownMenu(
                expanded = isExpanded xor it,
                onDismissRequest = { isExpanded = !isExpanded },
            ) {
                ThemeMode.entries.forEach { themeMode ->
                    RadioMenuItem(
                        title = stringResource(themeMode.key),
                        isSelected = mainViewModel.uiState.value.theme == themeMode,
                        onClick = {
                            isExpanded = !isExpanded
                            mainViewModel.setTheme(themeMode)
                        },
                    )
                }
            }
        },
    )

    return listOf(sort, theme)
}