package com.jsontextfield.departurescreen.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentWidth
import androidx.glance.semantics.contentDescription
import androidx.glance.semantics.semantics
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.entities.Trip

private const val SHOULD_SHOW_TRAIN_INFO = false

@Composable
fun WidgetTripListItem(
    trip: Trip,
    modifier: GlanceModifier = GlanceModifier,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val minutesContentDescription = context.resources.getQuantityString(
            R.plurals.minutes_content_description,
            trip.departureDiffMinutes,
            trip.departureDiffMinutes,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = trip.departureDiffMinutes.toString(),
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
            Text(
                text = context.getString(R.string.min),
                style = TextDefaults.defaultTextStyle.copy(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onBackground,
                ),
                maxLines = 1,
            )
        }
        Spacer(modifier = GlanceModifier.width(12.dp))
        WidgetTripCodeBox(
            tripCode = trip.code,
            modifier = GlanceModifier
                .size(32.dp)
                .background(
                    imageProvider = ImageProvider(R.drawable.squircle),
                    colorFilter = ColorFilter.tint(ColorProvider(trip.color))
                )
                .semantics {
                    contentDescription = if (trip.isBus) {
                        trip.code
                    } else {
                        trip.name
                    }
                }
        )
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
            if (trip.isCancelled) {
                Text(
                    text = context.getString(R.string.cancelled),
                    style = TextDefaults.defaultTextStyle.copy(
                        color = GlanceTheme.colors.error,
                        fontSize = 12.sp,
                    ),
                )
            } else if (trip.isExpress) {
                Text(
                    text = context.getString(R.string.express),
                    style = TextDefaults.defaultTextStyle.copy(
                        color = GlanceTheme.colors.primary,
                        fontSize = 12.sp,
                    ),
                )
            }
        }
        Spacer(modifier = GlanceModifier.width(12.dp))

        val platform = context.getString(R.string.platform, trip.platform)
        Column(
            modifier = GlanceModifier.width(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = trip.platform,
                maxLines = 3,
                modifier = GlanceModifier
                    .semantics {
                        if (trip.hasPlatform) {
                            contentDescription = platform
                        } else {
                            contentDescription = ""
                        }
                    },
                style = if (trip.hasPlatform) {
                    TextDefaults.defaultTextStyle.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary
                    )
                } else {
                    TextDefaults.defaultTextStyle.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        color = GlanceTheme.colors.onBackground
                    )
                },
            )

            if (trip.info.isNotBlank() && SHOULD_SHOW_TRAIN_INFO) {
                Text(
                    text = trip.info,
                    style = TextDefaults.defaultTextStyle.copy(
                        textAlign = TextAlign.Center,
                        color = if (trip.hasPlatform) {
                            GlanceTheme.colors.primary
                        } else {
                            GlanceTheme.colors.onBackground
                        }
                    ),
                )
            }
            trip.cars?.let {
                Text(
                    text = context.resources.getQuantityString(R.plurals.number_of_cars, it.toIntOrNull() ?: 0, it),
                    style = TextDefaults.defaultTextStyle.copy(
                        color = GlanceTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                    ),
                )
            } ?: trip.busType?.let {
                Text(
                    text = it,
                    style = TextDefaults.defaultTextStyle.copy(
                        color = GlanceTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                    ),
                )
            }
        }
    }
}