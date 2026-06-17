package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun OverflowMenuItem(
    icon: DrawableResource,
    tooltip: String = "",
    isVisible: Boolean = true,
    isAlertIndicatorVisible: Boolean = false,
    isChecked: Boolean = false,
    onClick: () -> Unit = {},
) {
    if (isVisible) {
        DropdownMenuItem(
            text = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = tooltip, modifier = Modifier.weight(1f))
                    if (isAlertIndicatorVisible) {
                        Badge()
                    }
                }
            },
            leadingIcon = { Icon(painterResource(icon), null) },
            onClick = onClick,
            modifier = Modifier.semantics(true) {}
        )
    }
}