package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jsontextfield.departurescreen.getScreenWidth
import com.jsontextfield.departurescreen.ui.MainViewModel
import com.jsontextfield.departurescreen.ui.SortMode
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.filter
import departure_screen.composeapp.generated.resources.more
import departure_screen.composeapp.generated.resources.sort
import org.jetbrains.compose.resources.stringResource

@Composable
fun ActionBar(
    actions: List<Action> = emptyList(),
) {
    val screenWidthDp = getScreenWidth()
    val maxActions = when {
        screenWidthDp < 400 -> screenWidthDp / 4 / 48
        screenWidthDp < 600 -> screenWidthDp / 3 / 48
        screenWidthDp < 800 -> screenWidthDp / 2 / 48
        else -> screenWidthDp * 2 / 3 / 48
    }

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
                }
                else {
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
        isVisible = mainViewModel.allTrains.value.isNotEmpty(),
        menuContent = {
            var isExpanded by remember { mutableStateOf(it) }
            DropdownMenu(
                expanded = isExpanded xor it,
                onDismissRequest = { isExpanded = !isExpanded },
            ) {
                SortMode.entries.forEach { sortMode ->
                    RadioMenuItem(
                        title = stringResource(sortMode.key),
                        isSelected = mainViewModel.sortMode.value == sortMode,
                        onClick = {
                            isExpanded = !isExpanded
                            mainViewModel.setSortMode(sortMode)
                        },
                    )
                }
            }
        },
    )
    val filter = Action(
        icon = Icons.Rounded.FilterList,
        tooltip = stringResource(Res.string.filter),
        isVisible = mainViewModel.allTrains.value.isNotEmpty(),
        onClick = {
            mainViewModel.showFilterDialog = true
        }
    )

    // TODO: Add theme change and about screen
//    val theme by mainViewModel.theme.collectAsStateWithLifecycle()
//    val darkMode = Action(
//        icon = Icons.Rounded.BrightnessMedium,
//        tooltip = stringResource(Res.string.change_theme),
//        menuContent = {
//            var isExpanded by remember { mutableStateOf(it) }
//            DropdownMenu(
//                expanded = isExpanded xor it,
//                onDismissRequest = { isExpanded = !isExpanded },
//            ) {
//                ThemeMode.entries.forEach { themeMode ->
//                    RadioMenuItem(
//                        title = stringResource(themeMode.key),
//                        isSelected = theme == themeMode,
//                        onClick = {
//                            isExpanded = !isExpanded
//                            mainViewModel.changeTheme(themeMode)
//                        },
//                    )
//                }
//            }
//        },
//    )

//    var showAboutDialog by remember { mutableStateOf(false) }
//
//    if (showAboutDialog) {
//        AboutDialog(
//            onLicences = {
//                //context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
//            },
//            onDismiss = { showAboutDialog = false },
//        )
//    }
//
//    val about = Action(
//        icon = Icons.Rounded.Info,
//        tooltip = stringResource(Res.string.about),
//        onClick = { showAboutDialog = true },
//    )

    return listOf(
        filter,
        sort,
//        about,
    )
}