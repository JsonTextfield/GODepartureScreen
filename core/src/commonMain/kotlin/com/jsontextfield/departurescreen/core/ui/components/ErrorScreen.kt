package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.error
import departure_screen.core.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorScreen(onRetry: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(stringResource(Res.string.error))
        Spacer(modifier = Modifier.height(8.dp))
        FilledTonalButton(onClick = onRetry) {
            Text(stringResource(Res.string.retry))
        }
    }
}