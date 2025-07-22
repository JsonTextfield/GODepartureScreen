package com.jsontextfield.departurescreen.ui

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.theme.trainColours
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.express
import departure_screen.composeapp.generated.resources.minutes
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource


class MyAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            MyContent()
        }
    }

    @Composable
    private fun MyContent() {

        val trains = List(7) {
            Train(
                id = (it + 1000).toString(),
                code = (it + 10).toString(),
                departureTime = Instant.fromEpochMilliseconds(it * 7894563L),
                destination = "Station $it",
                platform = "${it + 1} & ${it + 2}",
                color = trainColours.values.random(),
            )
        }
        LazyColumn(
            modifier = GlanceModifier.background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(trains) { train ->
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(Res.string.minutes, train.departureDiffMinutes),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Box(
                        modifier = GlanceModifier
                            .size(40.dp)
                            .background(train.color)
                            .cornerRadius(4.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            train.code,
                            style = TextStyle(
                                color = ColorProvider(Color.White),
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            train.destination,
                        )
                        if (train.isExpress) {
                            Text(stringResource(Res.string.express))
                        }
                    }
                    Text(train.platform)
                }
            }
        }
    }
}