package com.jsontextfield.departurescreen.android.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

@Composable
fun CountdownTimer(
    timeRemaining: Int = 30_000,
) {
    val progress by animateFloatAsState(
        targetValue = timeRemaining.toFloat() / 30_000,
        animationSpec = tween(
            durationMillis = if (timeRemaining == 30_000) 100 else 1100,
            easing = LinearEasing
        )
    )
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress },
        )
        Text((timeRemaining / 1000).toString())
    }
}