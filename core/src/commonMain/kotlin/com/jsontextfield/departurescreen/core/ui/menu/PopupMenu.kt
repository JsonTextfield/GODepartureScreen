package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable

@Composable
fun PopupMenu(
    showMenu: Boolean,
    onDismissRequest: () -> Unit,
    menuContent: @Composable () -> Unit,
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismissRequest
    ) {
        menuContent()
    }
}