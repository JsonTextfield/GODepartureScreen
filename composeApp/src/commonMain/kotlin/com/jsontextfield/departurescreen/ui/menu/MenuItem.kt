package com.jsontextfield.departurescreen.ui.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItem(
    icon: DrawableResource,
    tooltip: String = "",
    isVisible: Boolean = true,
    isAlertIndicatorVisible: Boolean = false,
    onClick: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = isVisible,
        content = {
            TooltipBox(
                state = rememberTooltipState(),
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    PlainTooltip {
                        Text(text = tooltip, modifier = Modifier.padding(4.dp))
                    }
                },
            ) {
                IconButton(
                    content = {
                        Box {
                            Icon(painterResource(icon), tooltip)
                            if (isAlertIndicatorVisible) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp).align(Alignment.TopEnd)
                                        .background(
                                            color = MaterialTheme.colorScheme.error,
                                            shape = CircleShape,
                                        )
                                )
                            }
                        }
                    },
                    onClick = onClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
        }
    )
}