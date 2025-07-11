package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.express
import departure_screen.composeapp.generated.resources.minutes
import departure_screen.composeapp.generated.resources.minutes_content_description
import departure_screen.composeapp.generated.resources.platform
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TrainListItem(
    train: Train,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val minutesContentDescription = pluralStringResource(
            Res.plurals.minutes_content_description,
            train.departureDiffMinutes.toInt(),
            train.departureDiffMinutes.toInt(),
        )
        Text(
            stringResource(Res.string.minutes, train.departureDiffMinutes),
            style = MaterialTheme.typography.labelMedium.copy(
                textAlign = TextAlign.Center,
                fontWeight = if (train.departureDiffMinutes < 6) FontWeight.Bold else FontWeight.Normal,
            ),
            maxLines = 2,
            modifier = Modifier
                .weight(.1f)
                .semantics {
                    contentDescription = minutesContentDescription
                },
        )
        TrainCodeBox(
            train.code,
            modifier = Modifier
                .size(40.dp)
                .background(color = train.color, shape = RoundedCornerShape(4.dp))
                .semantics {
                    contentDescription = train.name
                },
        )
        Column(modifier = Modifier.weight(.5f)) {
            Text(
                train.destination,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (train.isExpress) {
                Text(
                    stringResource(Res.string.express),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                )
            }
        }
        val platform = stringResource(Res.string.platform, train.platform)
        Text(
            train.platform,
            modifier = Modifier
                .weight(.25f)
                .clearAndSetSemantics {
                    if (train.hasArrived) {
                        contentDescription = platform
                    }
                },
            style = MaterialTheme.typography.titleMedium.run {
                copy(
                    textAlign = TextAlign.Center,
                    fontWeight = if (train.hasArrived) FontWeight.Bold else fontWeight,
                    color = if (train.hasArrived) MaterialTheme.colorScheme.primary else color
                )
            },
        )
    }
}