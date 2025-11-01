package com.jsontextfield.departurescreen.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.entities.Trip

@Composable
fun WidgetTripRow(
    trip: Trip,
    modifier: GlanceModifier = GlanceModifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = "${trip.departureDiffMinutes}\nmin",
            style = TextDefaults.defaultTextStyle.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.onBackground,
            ),
        )
        Spacer(modifier = GlanceModifier.width(12.dp))
        Box(
            modifier = GlanceModifier
                .size(32.dp)
                .background(
                    imageProvider = ImageProvider(R.drawable.squircle),
                    colorFilter = ColorFilter.tint(ColorProvider(trip.color))
                )
                .cornerRadius(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = trip.code,
                style = TextDefaults.defaultTextStyle.copy(
                    color = ColorProvider(Color.White),
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
        Spacer(modifier = GlanceModifier.width(12.dp))
        Column(
            modifier = GlanceModifier.wrapContentWidth().defaultWeight(),
        ) {
            Text(
                text = trip.destination,
                maxLines = 2,
                style = TextDefaults.defaultTextStyle.copy(
                    color = GlanceTheme.colors.onBackground,
                )
            )
            val context = LocalContext.current
            if (trip.isCancelled) {
                Text(
                    text = context.getString(R.string.cancelled),
                    style = TextDefaults.defaultTextStyle.copy(
                        color = GlanceTheme.colors.error,
                    ),
                )
            }
            else if (trip.isExpress) {
                Text(
                    text = context.getString(R.string.express),
                    style = TextDefaults.defaultTextStyle.copy(
                        color = GlanceTheme.colors.primary
                    ),
                )
            }
        }
        Spacer(modifier = GlanceModifier.width(12.dp))
        Text(
            text = trip.platform,
            style = TextDefaults.defaultTextStyle.copy(
                textAlign = TextAlign.Center,
                fontWeight = if (trip.hasPlatform) {
                    FontWeight.Bold
                }
                else {
                    FontWeight.Normal
                },
                color = if (trip.hasPlatform) {
                    GlanceTheme.colors.primary
                }
                else {
                    GlanceTheme.colors.onBackground
                }
            ),
            modifier = GlanceModifier.width(60.dp),
        )
    }
}