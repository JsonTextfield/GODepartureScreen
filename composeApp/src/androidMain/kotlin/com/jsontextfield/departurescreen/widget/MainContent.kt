package com.jsontextfield.departurescreen.widget

import androidx.compose.runtime.Composable
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
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.viewmodels.WidgetUIState
import com.jsontextfield.departurescreen.ui.MainActivity

@Composable
fun MainContent(
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState.status) {
            Status.ERROR -> {
                Text(
                    text = context.getString(R.string.error),
                    style = TextDefaults.defaultTextStyle.copy(
                        textAlign = TextAlign.Center,
                        color = GlanceTheme.colors.onBackground,
                    ),
                )
                Spacer(modifier = GlanceModifier.height(8.dp))
                Button(context.getString(R.string.retry), onClick = onRefresh)
            }

            Status.LOADING -> {
                CircularProgressIndicator(color = GlanceTheme.colors.primary)
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

                RefreshButton(
                    title = context.getString(R.string.updated, uiState.lastUpdated),
                    onClick = onRefresh,
                )
            }
        }
    }
}