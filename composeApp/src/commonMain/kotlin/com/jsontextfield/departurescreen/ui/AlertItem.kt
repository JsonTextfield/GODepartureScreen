package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.entities.Alert
import com.jsontextfield.departurescreen.data.GoTrainDataSource.Companion.inFormatter
import kotlinx.datetime.format


@Composable
fun AlertItem(
    alert: Alert,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = alert.subject,
            modifier = modifier.semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = alert.date.format(inFormatter),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = alert.body,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}