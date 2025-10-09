package com.jsontextfield.departurescreen.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.theme.darkScheme
import com.jsontextfield.departurescreen.core.ui.theme.lightScheme
import com.jsontextfield.departurescreen.core.ui.viewmodels.WidgetUIState
import com.jsontextfield.departurescreen.ui.MainActivity

@Composable
fun MyContent(
    uiState: WidgetUIState,
    onRefresh: () -> Unit = {},
) {
    val context = LocalContext.current
    GlanceTheme(
        colors = ColorProviders(
            light = lightScheme,
            dark = darkScheme,
        )
    ) {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
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
                        Text(stringResource(R.string.error))
                        Spacer(modifier = GlanceModifier.height(8.dp))
                        Button(stringResource(R.string.retry), onClick = onRefresh)
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
                                .padding(12.dp)
                                .clickable(actionStartActivity<MainActivity>()),
                        )

                        LazyColumn(modifier = GlanceModifier.defaultWeight()) {
                            items(uiState.allTrips) { trip ->
                                WidgetTripRow(trip)
                            }
                        }

                        Text(
                            text = context.getString(R.string.updated, uiState.lastUpdated),
                            style = TextDefaults.defaultTextStyle.copy(
                                textAlign = TextAlign.Center,
                                color = GlanceTheme.colors.onBackground,
                            ),
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable { onRefresh() },
                        )
                    }
                }
            }
        }
    }
}