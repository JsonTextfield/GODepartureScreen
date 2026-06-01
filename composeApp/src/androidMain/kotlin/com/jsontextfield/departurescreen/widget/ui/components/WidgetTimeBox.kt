package com.jsontextfield.departurescreen.widget.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.width
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.TimeFormat

@Composable
fun WidgetTimeBox(
    trip: Trip,
    timeFormat: TimeFormat = TimeFormat.RELATIVE,
) {
    val context = LocalContext.current
    val minutesContentDescription = context.resources.getQuantityString(
        R.plurals.minutes_content_description,
        trip.relativeDepartureTime,
        trip.relativeDepartureTime,
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier.width(40.dp)
    ) {
        Text(
            text = when (timeFormat) {
                TimeFormat.TWELVE_HOUR -> trip.twelveHourDepartureTime
                TimeFormat.TWENTY_FOUR_HOUR -> trip.twentyFourHourDepartureTime
                TimeFormat.RELATIVE -> trip.relativeDepartureTime.toString()
            },
            style = TextDefaults.defaultTextStyle.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onBackground,
            ),
            modifier = GlanceModifier
                .semantics {
                    contentDescription = minutesContentDescription
                }
        )
        if (timeFormat == TimeFormat.RELATIVE) {
            Text(
                text = context.getString(R.string.min),
                style = TextDefaults.defaultTextStyle.copy(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onBackground,
                ),
                maxLines = 1,
            )
        }
    }
}