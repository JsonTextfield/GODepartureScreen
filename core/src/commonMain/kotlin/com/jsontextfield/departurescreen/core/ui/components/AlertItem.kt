@file:OptIn(FormatStringsInDatetimeFormats::class)

package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.day_difference
import departure_screen.core.generated.resources.hour_difference
import departure_screen.core.generated.resources.minute_difference
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import org.jetbrains.compose.resources.stringResource


@Composable
fun AlertItem(
    alert: Alert,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val language = Locale.current.language
    val fontScale = LocalDensity.current.fontScale
    Card(
        modifier = modifier.clickable(
            enabled = false, // alert.urlEn != null || alert.urlFr != null,
            onClick = onClick,
        ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        border = BorderStroke(
            width = Hairline,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f),
        ),
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .semantics(mergeDescendants = true) {},
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = alert.affectedStops.takeIf { it.isNotEmpty() }
                            ?.joinToString(", ", postfix = ": ").orEmpty() + alert.getSubject(language),
                        modifier = Modifier
                            .weight(10 / 12f)
                            .semantics { heading() },
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        text = if (alert.dateDifference.inWholeDays > 0) {
                            stringResource(Res.string.day_difference, alert.dateDifference.inWholeDays)
                        } else if (alert.dateDifference.inWholeHours > 0) {
                            stringResource(Res.string.hour_difference, alert.dateDifference.inWholeHours)
                        } else {
                            stringResource(Res.string.minute_difference, alert.dateDifference.inWholeMinutes)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(2 / 12f)
                    )
                    if (!alert.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error,
                                    shape = CircleShape,
                                )
                        )
                    }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (line in alert.affectedLines) {
                        TripCodeBox(
                            tripCode = line,
                            modifier = Modifier
                                .size((MaterialTheme.typography.titleMedium.fontSize.value * fontScale * 2).dp)
                                .background(
                                    color = lineColours[line] ?: Color.Gray,
                                    shape = SquircleShape,
                                )
                        )
                    }
                }
                Text(
                    text = alert.getBody(language),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}