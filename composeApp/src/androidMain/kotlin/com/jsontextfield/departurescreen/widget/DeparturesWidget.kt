package com.jsontextfield.departurescreen.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults
import androidx.glance.unit.ColorProvider
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.data.IGoTrainDataSource
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.domain.DepartureScreenUseCase
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.theme.darkScheme
import com.jsontextfield.departurescreen.core.ui.theme.lightScheme
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import com.jsontextfield.departurescreen.core.ui.viewmodels.WidgetUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.WidgetViewModel
import com.jsontextfield.departurescreen.ui.MainActivity
import kotlinx.datetime.Instant
import org.koin.java.KoinJavaComponent.inject
import kotlin.time.Duration.Companion.minutes

class DeparturesWidget : GlanceAppWidget() {
    val preferencesRepository: IPreferencesRepository by inject(IPreferencesRepository::class.java)
    val goTrainDataSource: IGoTrainDataSource by inject(IGoTrainDataSource::class.java)
    val departureScreenUseCase = DepartureScreenUseCase(
        goTrainDataSource = goTrainDataSource,
        preferencesRepository = preferencesRepository,
    )
    val viewModel = WidgetViewModel(
        departureScreenUseCase = departureScreenUseCase,
        goTrainDataSource = goTrainDataSource,
        preferencesRepository = preferencesRepository,
    )

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val uiState = WidgetUIState(
            status = Status.LOADED,
            selectedStation = Station(
                name = "Union Station GO",
                code = "UN",
                type = "Train Station",
            ),
            _allTrips = listOf(
                Trip(
                    id = "X0000",
                    code = "LW",
                    destination = "West Harbour GO",
                    name = "Lakeshore West",
                    color = lineColours["LW"] ?: Color.Gray,
                    isBus = false,
                    isCancelled = false,
                    platform = "7 & 8",
                    departureTime = Instant.fromEpochMilliseconds(16.minutes.inWholeMilliseconds),
                    lastUpdated = Instant.fromEpochMilliseconds(0),
                )
            )
        )

        provideContent {
            DepartureScreenWidget(uiState)
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            val uiState by viewModel.uiState.collectAsState()
            DepartureScreenWidget(uiState, viewModel::refresh)
        }
    }
}

@Composable
fun DepartureScreenWidget(
    uiState: WidgetUIState,
    onRefresh: () -> Unit = {},
) {
    GlanceTheme(
        colors = ColorProviders(
            light = lightScheme,
            dark = darkScheme,
        )
    ) {
        val context = LocalContext.current
        Scaffold(
            titleBar = {
                TitleBar(
                    startIcon = ImageProvider(R.mipmap.ic_launcher),
                    iconColor = null,
                    title = uiState.selectedStation?.name.orEmpty(),
                    modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>()),
                )
            },
            horizontalPadding = 0.dp,
            backgroundColor = ColorProvider(GlanceTheme.colors.background.getColor(context).copy(alpha = .8f))
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
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
                        LazyColumn(modifier = GlanceModifier.defaultWeight()) {
                            items(uiState.allTrips) { trip ->
                                WidgetTripListItem(
                                    trip,
                                    modifier = GlanceModifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                )
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
    }
}
