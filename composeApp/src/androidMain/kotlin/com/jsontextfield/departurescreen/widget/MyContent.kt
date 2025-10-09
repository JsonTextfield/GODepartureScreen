package com.jsontextfield.departurescreen.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.viewmodels.WidgetUIState
import com.jsontextfield.departurescreen.ui.MainActivity

@Composable
fun MyContent(
    uiState: WidgetUIState,
    onRefresh: () -> Unit = {},
) {
    val context = LocalContext.current
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(
                color = GlanceTheme.colors.background.getColor(context).copy(alpha = .8f)
            )
            .appWidgetBackground(),
    ) {
        when (uiState.status) {
            Status.ERROR -> {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        context.getString(R.string.error),
                        style = TextDefaults.defaultTextStyle.copy(
                            textAlign = TextAlign.Center,
                            color = GlanceTheme.colors.onBackground,
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    Button(context.getString(R.string.retry), onClick = onRefresh)
                }
            }

            Status.LOADING -> {
                Box(
                    modifier = GlanceModifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = GlanceTheme.colors.primary)
                }
            }

            Status.LOADED -> {
                Text(
                    text = uiState.selectedStation?.name.orEmpty(),
                    style = TextDefaults.defaultTextStyle.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = GlanceTheme.colors.onBackground,
                    ),
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                )

                LazyColumn(modifier = GlanceModifier.defaultWeight()) {
                    items(uiState.allTrips) { trip ->
                        WidgetTripRow(trip)
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onRefresh() },
                ) {
                    Image(
                        ImageProvider(R.drawable.refresh_24px),
                        null,
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = context.getString(R.string.updated, uiState.lastUpdated),
                        style = TextDefaults.defaultTextStyle.copy(
                            textAlign = TextAlign.Center,
                            color = GlanceTheme.colors.onBackground,
                        ),
                    )
                }
            }
        }
    }
}