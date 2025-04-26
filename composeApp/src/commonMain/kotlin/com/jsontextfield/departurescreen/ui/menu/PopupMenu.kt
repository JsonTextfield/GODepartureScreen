package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.runtime.Composable

@Composable
fun PopupMenu(
    showMenu: Boolean = false,
    menuContent: @Composable (Boolean) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    menuContent(showMenu)
    content()
}