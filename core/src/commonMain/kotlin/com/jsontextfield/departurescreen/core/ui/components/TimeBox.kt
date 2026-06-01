package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.min
import departure_screen.core.generated.resources.minutes_content_description
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimeBox(
    trip: Trip,
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat = TimeFormat.RELATIVE,
) {

    val minutesContentDescription = pluralStringResource(
        Res.plurals.minutes_content_description,
        trip.relativeDepartureTime,
        trip.relativeDepartureTime,
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
                TimeFormat.TWELVE_HOUR -> trip.twelveHourDepartureTime
                TimeFormat.TWENTY_FOUR_HOUR -> trip.twentyFourHourDepartureTime
                TimeFormat.RELATIVE -> trip.relativeDepartureTime.toString()
            },
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(),
            style = MaterialTheme.typography.titleMedium.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
        )
        if (timeFormat == TimeFormat.RELATIVE) {
            Text(
                text = stringResource(Res.string.min),
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelSmall.copy(
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
            )
        }
    }
}