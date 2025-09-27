@file:OptIn(FormatStringsInDatetimeFormats::class)

package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Alert
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern


@Composable
fun AlertItem(
    alert: Alert,
    modifier: Modifier = Modifier,
) {
    Card {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .semantics(mergeDescendants = true) {},
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = alert.subject,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = alert.date.format(Format {
                    byUnicodePattern("HH:mm, d/MM/yyyy")
                }),
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = alert.body,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}