package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.theme.richmondHill
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TrainListItem(train: Train, useAlternateColor: Boolean = false) {
    ListItem(
        tonalElevation = if (useAlternateColor) 1.dp else 0.dp,
        headlineContent = { Text(train.destination) },
        supportingContent = {
            Text(
                train.info,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontStyle = FontStyle.Italic,
                ),
            )
        },
        leadingContent = {
            TrainCodeIcon(
                code = train.code,
                colour = train.color,
            )
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    train.platform,
                    style = MaterialTheme.typography.titleMedium.copy(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                    )
                )
                Text(
                    train.departureTimeString,
                    style = MaterialTheme.typography.labelMedium.copy(
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    )
}

@Composable
fun TrainListItem2(train: Train, useAlternateColor: Boolean = false) {
    Surface(
        tonalElevation = if (useAlternateColor) 1.dp else 0.dp
    ) {
        Row(
            Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                train.departureTimeString,
                style = MaterialTheme.typography.labelMedium.copy(
                    textAlign = TextAlign.Center,
                ),
            )
            TrainCodeIcon(
                code = train.code,
                colour = train.color,
            )
            Text(
                train.destination,
                maxLines = 2,
                modifier = Modifier.weight(.5f),
            )
            Text(
                train.platform,
                modifier = Modifier.weight(.2f),
                style = MaterialTheme.typography.titleMedium.run {
                    copy(
                        textAlign = TextAlign.Center,
                        fontWeight = if (train.hasArrived) FontWeight.Bold else fontWeight,
                        color = if (train.hasArrived) Color.Green else color
                    )
                }
            )
        }
    }
}

@Composable
private fun TrainCodeIcon(
    code: String,
    colour: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(30.dp)
            .background(color = colour, shape = RoundedCornerShape(4.dp))
    ) {
        Text(
            code,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
            )
        )
    }
}

@Preview
@Composable
fun TrainListItemPreview() {
    val train = Train(
        destination = "Bloomington GO",
        platform = "4 & 5",
        code = "RH",
        departureTimeString = "12:34",
        info = "Wait / Attendez",
        color = richmondHill,
    )
    TrainListItem(train)
}