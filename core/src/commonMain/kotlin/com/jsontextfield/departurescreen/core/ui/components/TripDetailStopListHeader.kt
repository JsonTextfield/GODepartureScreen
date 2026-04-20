package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.stop
import departure_screen.core.generated.resources.time
import org.jetbrains.compose.resources.stringResource

@Composable
fun TripDetailStopListHeader(
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = stringResource(Res.string.time),
            modifier = Modifier.width(80.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                textAlign = TextAlign.Center,
            ),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(Res.string.stop),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}