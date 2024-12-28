package com.jsontextfield.departurescreen.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Timer(
    totalMs: Int,
    remainingMs: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { remainingMs.toFloat() / totalMs.toFloat() },
        )
        Text(text = (remainingMs / 1000).toString())
    }
}