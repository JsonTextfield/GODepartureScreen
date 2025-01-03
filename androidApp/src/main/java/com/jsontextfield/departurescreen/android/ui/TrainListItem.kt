package com.jsontextfield.departurescreen.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.theme.RichmondHill

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
                colour = Color(train.color),
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
                    train.departureTime,
                    style = MaterialTheme.typography.labelMedium.copy(
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    )
}

@Composable
private fun TrainCodeIcon(code: String, colour: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .background(color = colour, shape = CircleShape)
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
        departureTime = "12:34",
        info = "Wait / Attendez",
        color = RichmondHill,
    )
    TrainListItem(train)
}