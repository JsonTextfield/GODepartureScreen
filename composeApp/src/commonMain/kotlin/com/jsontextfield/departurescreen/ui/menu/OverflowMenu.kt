package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun OverflowMenu(
    isExpanded: Boolean = false,
    actions: List<Action>,
    onItemSelected: () -> Unit = {},
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { onItemSelected() },
    ) {
        for (action in actions) {
            if (action.menuContent != null) {
                var showMenu by remember { mutableStateOf(false) }
                PopupMenu(
                    showMenu = showMenu,
                    menuContent = action.menuContent,
                    content = {
                        OverflowMenuItem(
                            icon = action.icon,
                            tooltip = action.tooltip,
                        ) {
                            showMenu = !showMenu
                        }
                    },
                )
            }
            else {
                OverflowMenuItem(
                    icon = action.icon,
                    tooltip = action.tooltip,
                    isVisible = action.isVisible,
                    isChecked = action.isChecked,
                ) {
                    action.onClick()
                    onItemSelected()
                }
            }
        }
    }
}