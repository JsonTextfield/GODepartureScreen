package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun OverflowMenuItem(
    icon: DrawableResource,
    tooltip: String = "",
    isVisible: Boolean = true,
    isChecked: Boolean = false,
    onClick: () -> Unit = {},
) {
    if (isVisible) {
        DropdownMenuItem(
            text = { Text(text = tooltip) },
            leadingIcon = { Icon(painterResource(icon), null) },
            onClick = onClick,
            modifier = Modifier.semantics(true){}
        )
    }
}