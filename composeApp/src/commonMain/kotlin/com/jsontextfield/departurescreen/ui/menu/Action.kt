package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource

data class Action(
    val icon: DrawableResource,
    val tooltip: String,
    val onClick: (() -> Unit)? = null,
    val menuContent: (@Composable (onDismiss: () -> Unit) -> Unit)? = null,
    val isVisible: Boolean = true,
)