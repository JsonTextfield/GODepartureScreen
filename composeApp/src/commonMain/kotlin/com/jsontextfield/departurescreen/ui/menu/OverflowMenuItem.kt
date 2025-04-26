package com.textfield.json.ottawastreetcameras.ui.components.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics

@Composable
fun OverflowMenuItem(
    icon: ImageVector,
    tooltip: String = "",
    isVisible: Boolean = true,
    isChecked: Boolean = false,
    onClick: () -> Unit = {},
) {
    if (isVisible) {
        DropdownMenuItem(
            text = { Text(text = tooltip) },
            leadingIcon = { Icon(icon, null) },
            trailingIcon = {
                if (isChecked) {
                    Icon(Icons.Rounded.Check, null)
                }
            },
            onClick = onClick,
            modifier = Modifier.semantics(true){}
        )
    }
}