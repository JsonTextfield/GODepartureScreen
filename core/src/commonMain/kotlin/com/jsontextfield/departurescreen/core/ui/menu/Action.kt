package com.jsontextfield.departurescreen.core.ui.menu

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class Action(
    val icon: ImageVector,
    val tooltip: String,
    val onClick: (() -> Unit)? = null,
    val menuContent: (@Composable (onDismiss: () -> Unit) -> Unit)? = null,
    val isVisible: Boolean = true,
)