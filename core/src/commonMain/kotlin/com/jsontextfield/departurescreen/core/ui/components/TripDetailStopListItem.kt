package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Schedule
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.min
import departure_screen.core.generated.resources.minutes_content_description
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TripDetailStopListItem(
    stop: Schedule,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.RELATIVE,
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        TripDetailsTimeBox(
            timeFormat = timeFormat,
            relativeTime = stop.relativeDepartureTime,
            twelveHourTime = stop.twelveHourDepartureTime,
            twentyFourHourTime = stop.twentyFourHourDepartureTime,
            isEnabled = isEnabled,
            isSelected = isSelected,
            modifier = Modifier.width(80.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stop.name,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (isEnabled) 1f else 0.5f),

            style = if (isSelected) {
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                MaterialTheme.typography.bodyMedium
            },
        )
    }
}

@Composable
private fun TripDetailsTimeBox(
    relativeTime: Int,
    twelveHourTime: String,
    twentyFourHourTime: String,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.TWENTY_FOUR_HOUR,
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
) {
    val minutesContentDescription = pluralStringResource(
        Res.plurals.minutes_content_description,
        relativeTime,
        relativeTime,
    )

    Column(
        modifier = modifier
            .semantics {
                contentDescription = minutesContentDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = when (timeFormat) {
                TimeFormat.TWELVE_HOUR -> twelveHourTime
                TimeFormat.TWENTY_FOUR_HOUR -> twentyFourHourTime
                TimeFormat.RELATIVE -> relativeTime.toString()
            },
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee()
                .alpha(if (isEnabled) 1f else 0.5f),
            style = if (isSelected) {
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                MaterialTheme.typography.bodyMedium
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        if (timeFormat == TimeFormat.RELATIVE) {
            Text(
                text = stringResource(Res.string.min),
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (isEnabled) 1f else 0.5f),
                style = if (isSelected) {
                    MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    MaterialTheme.typography.labelSmall
                },
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}