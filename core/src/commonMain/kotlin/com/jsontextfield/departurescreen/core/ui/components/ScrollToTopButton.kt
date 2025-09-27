package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.scroll_to_top
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScrollToTopButton(
    isVisible: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = SquircleShape
        ) {
            Icon(
                Icons.Rounded.ArrowUpward,
                contentDescription = stringResource(Res.string.scroll_to_top),
            )
        }
    }
}