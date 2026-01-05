package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.rounded_arrow_upward_24
import departure_screen.core.generated.resources.scroll_to_top
import org.jetbrains.compose.resources.painterResource
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
            shape = SquircleShape,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painterResource(Res.drawable.rounded_arrow_upward_24),
                contentDescription = stringResource(Res.string.scroll_to_top),
            )
        }
    }
}