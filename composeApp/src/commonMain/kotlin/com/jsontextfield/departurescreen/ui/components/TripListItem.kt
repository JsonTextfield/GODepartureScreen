package com.jsontextfield.departurescreen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.entities.Trip
import com.jsontextfield.departurescreen.ui.SquircleShape
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.cancelled
import departure_screen.composeapp.generated.resources.express
import departure_screen.composeapp.generated.resources.min
import departure_screen.composeapp.generated.resources.minutes_content_description
import departure_screen.composeapp.generated.resources.platform
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.E

@Composable
fun TripListItem(
    trip: Trip,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val minutesContentDescription = pluralStringResource(
            Res.plurals.minutes_content_description,
            trip.departureDiffMinutes,
            trip.departureDiffMinutes,
        )
        val shouldShowTrainCode = with(LocalDensity.current) {
            fontScale <= 1.5f
        }
        Column(
            modifier = Modifier
                .weight(3 / 12f)
                .semantics {
                    contentDescription = minutesContentDescription
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = trip.departureDiffMinutes.toString(),
                modifier = Modifier.basicMarquee(),
                style = MaterialTheme.typography.titleMedium.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
            )
            Text(
                text = stringResource(Res.string.min),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
        if (shouldShowTrainCode) {
            val fontScale = LocalDensity.current.fontScale
            TripCodeBox(
                trip.code,
                modifier = Modifier
                    .size((MaterialTheme.typography.titleMedium.fontSize.value * fontScale * 2).dp)
                    .background(color = trip.color, shape = SquircleShape(E))
                    .semantics {
                        contentDescription = if (trip.isBus) {
                            trip.code
                        } else {
                            trip.name
                        }
                    },
            )
        }
        Column(modifier = Modifier.weight(6 / 12f)) {
            if (!shouldShowTrainCode) {
                Text(
                    text = trip.name,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Text(
                text = trip.destination,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (trip.isCancelled) {
                Text(
                    text = stringResource(Res.string.cancelled),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                )
            } else if (trip.isExpress) {
                Text(
                    text = stringResource(Res.string.express),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        val platform = stringResource(Res.string.platform, trip.platform)
        Text(
            text = trip.platform,
            maxLines = 3,
            modifier = Modifier
                .weight(3 / 12f)
                .clearAndSetSemantics {
                    if (trip.hasPlatform) {
                        contentDescription = platform
                    }
                },
            textAlign = TextAlign.Center,
            style = if (trip.hasPlatform) {
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                MaterialTheme.typography.titleMedium
            },
        )
    }
}