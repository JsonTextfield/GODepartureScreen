package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay

@Composable
fun CountdownTimer(
    timeRemaining: () -> Int = { 20_000 },
    totalTime: Int = 20_000,
) {
    val time = timeRemaining()
    val progress by animateFloatAsState(
        targetValue = time.toFloat() / totalTime,
        animationSpec = tween(
            durationMillis = if (time == totalTime) 100 else 1100,
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
        Text(
            (time / 1000).toString(),
            style = MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center)
        )
    }
}

@Composable
fun CountdownTimer(
    totalTimeMs: Long = 20_000L,
    isRepeating: Boolean = false,
    onTimerExpired: () -> Unit = {},
) {
    var timeRemaining by rememberSaveable { mutableLongStateOf(0) }
    val progress by animateFloatAsState(
        targetValue = timeRemaining.toFloat() / totalTimeMs,
        animationSpec = tween(
            durationMillis = if (timeRemaining == totalTimeMs) 100 else 1100,
            easing = EaseOutQuad,
        )
    )
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            if (timeRemaining < 1000L) {
                onTimerExpired()
                if (isRepeating) {
                    timeRemaining = totalTimeMs
                } else {
                    break
                }
            } else {
                timeRemaining -= 1000L
            }
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.clearAndSetSemantics {},
    ) {
        CircularProgressIndicator(
            progress = { progress },
        )
        Text(
            (timeRemaining / 1000).toString(),
            style = MaterialTheme.typography.labelMedium.copy(textAlign = TextAlign.Center)
        )
    }
}