package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class Action(
    val icon: ImageVector,
    val tooltip: String,
    val isVisible: Boolean = true,
    val isChecked: Boolean = false,
    val onClick: () -> Unit = {},
    val menuContent: (@Composable (expanded: Boolean) -> Unit)? = null,
)