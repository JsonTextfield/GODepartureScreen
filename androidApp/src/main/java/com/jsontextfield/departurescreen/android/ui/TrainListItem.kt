package com.jsontextfield.departurescreen.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train

@Composable
fun TrainListItem(train: Train, useAlternateColor: Boolean = false) {
    ListItem(
        tonalElevation = if (useAlternateColor) 1.dp else 0.dp,
        headlineContent = { Text(train.destination) },
        leadingContent = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .background(color = Color(train.colour))
            ) {
                Text(
                    train.code,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(train.platform, fontWeight = FontWeight.Bold)
                Text(train.departureTime, maxLines = 2, textAlign = TextAlign.Center)
            }
        }
    )
}

@Preview
@Composable
fun TrainListItemPreview() {
    val train = Train(
    ).apply {
        destination = "Bloomington GO"
        platform = "4 & 5"
        code = "RH"
        departureTime = "12:34"
        colour = 0xFF0099C7.toInt()
    }
    TrainListItem(train)
}

@Preview
@Composable
fun TrainListPreview() {
    Column {
        TrainLine.entries.map {
            TrainListItem(
                Train(
                ).apply {
                    destination = it.title
                    code = it.code
                    colour = it.colour
                    departureTime = "12:34"
                    platform = "4 & 5"
                }
            )
        }
    }
}