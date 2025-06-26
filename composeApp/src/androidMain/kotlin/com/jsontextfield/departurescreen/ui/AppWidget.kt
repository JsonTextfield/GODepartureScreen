package com.jsontextfield.departurescreen.ui

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
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
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import com.jsontextfield.departurescreen.Train
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.express
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
        val trains = List(11) {
            Train(
                id = (it + 1000).toString(),
                code = (it + 10).toString(),
                departureTime = Instant.fromEpochMilliseconds(it * 7894563L),
                destination = "Station $it",
                platform = "${it + 1} & ${it + 2}",
            )
        }
        LazyColumn(
            modifier = GlanceModifier.background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(trains) { train ->
                Spacer(modifier = GlanceModifier.height(8.dp))
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(train.departureTimeString)
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Box(
                        modifier = GlanceModifier.size(40.dp).background(train.color),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(train.code)
                    }
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Column {
                        Text(
                            train.destination,
                            modifier = GlanceModifier.defaultWeight(),
                        )
                        if (train.isExpress) {
                            Text(stringResource(Res.string.express))
                        }
                    }
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        train.platform,
                        modifier = GlanceModifier.defaultWeight(),
                    )
                }
            }
        }
    }
}