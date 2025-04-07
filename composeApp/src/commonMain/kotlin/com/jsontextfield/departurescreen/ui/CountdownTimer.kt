package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.sp

@Composable
fun CountdownTimer(
    timeRemaining: Int = 20_000,
) {
    val progress by animateFloatAsState(
        targetValue = timeRemaining.toFloat() / 20_000,
        animationSpec = tween(
            durationMillis = if (timeRemaining == 20_000) 100 else 1100,
            easing = LinearEasing
        )
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clearAndSetSemantics {},
    ) {
        CircularProgressIndicator(
            progress = { progress },
        )
        Text((timeRemaining / 1000).toString(), fontSize = 12.sp)
    }
}