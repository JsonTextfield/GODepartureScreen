@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.widget.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
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
import com.jsontextfield.departurescreen.core.data.IPreferencesRepository
import com.jsontextfield.departurescreen.core.data.ITransitRepository
import com.jsontextfield.departurescreen.core.domain.GetSelectedStopUseCase
import com.jsontextfield.departurescreen.core.entities.Stop
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.StopType
import com.jsontextfield.departurescreen.core.ui.theme.darkScheme
import com.jsontextfield.departurescreen.core.ui.theme.lightScheme
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import com.jsontextfield.departurescreen.ui.MainActivity
import com.jsontextfield.departurescreen.widget.config.WidgetConfig
import com.jsontextfield.departurescreen.widget.config.WidgetConfigDataStore
import com.jsontextfield.departurescreen.widget.ui.components.RefreshButton
import com.jsontextfield.departurescreen.widget.ui.components.WidgetTripListHeaderRow
import com.jsontextfield.departurescreen.widget.ui.components.WidgetTripListItem
import org.koin.java.KoinJavaComponent.inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class DeparturesWidget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Responsive(
        sizes = setOf(
            DpSize(210.dp, 120.dp),
            DpSize(480.dp, 240.dp),
            DpSize(640.dp, 480.dp),
            DpSize(1000.dp, 640.dp),
        )
    )

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val uiState = WidgetUIState(
            status = Status.LOADED,
            selectedStop = Stop(
                name = "Union Station GO",
                code = "UN",
                types = setOf(StopType.TRAIN),
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
        val preferencesRepository: IPreferencesRepository by inject(IPreferencesRepository::class.java)
        val goTrainDataSource: ITransitRepository by inject(ITransitRepository::class.java)
        val configDataStore: WidgetConfigDataStore by inject(WidgetConfigDataStore::class.java)
        val getSelectedStopUseCase = GetSelectedStopUseCase(
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
        )
        val viewModel = WidgetViewModel(
            getSelectedStopUseCase = getSelectedStopUseCase,
            goTrainDataSource = goTrainDataSource,
            preferencesRepository = preferencesRepository,
            configDataStore = configDataStore,
        )
        val dataStore = WidgetConfigDataStore(context)
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val appWidgetId = glanceAppWidgetManager.getAppWidgetId(id)

        viewModel.loadConfig(appWidgetId)

        provideContent {
            // create your AppWidget here
            val uiState by viewModel.uiState.collectAsState()
            val dataStoreWidgetConfig by dataStore.getConfig(appWidgetId).collectAsState(WidgetConfig())
            // Use the in-memory companion map if available; otherwise use the stored config
            DepartureScreenWidget(uiState, dataStoreWidgetConfig, viewModel::refresh)
        }
    }
}

@Composable
fun DepartureScreenWidget(
    uiState: WidgetUIState,
    config: WidgetConfig? = null,
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
                    title = uiState.selectedStop?.name.orEmpty(),
                    modifier = GlanceModifier.clickable(
                        actionStartActivity<MainActivity>(
                            parameters = actionParametersOf(
                                selectedStopKey to uiState.selectedStop?.code.orEmpty()
                            )
                        ),
                    ),
                )
            },
            horizontalPadding = 0.dp,
            backgroundColor = ColorProvider(
                GlanceTheme.colors.background.getColor(context).copy(alpha = config?.opacity ?: .8f)
            )
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
                        Box(
                            modifier = GlanceModifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = context.getString(R.string.loading),
                                style = TextDefaults.defaultTextStyle.copy(color = GlanceTheme.colors.onBackground)
                            )
                        }
                    }

                    Status.LOADED -> {
                        val size = LocalSize.current
                        val columns = (size.width / 240.dp).toInt().coerceIn(1, 4)
                        LazyVerticalGrid(
                            gridCells = GridCells.Fixed(columns),
                            modifier = GlanceModifier.defaultWeight(),
                        ) {
                            items(columns) {
                                WidgetTripListHeaderRow(
                                    modifier = GlanceModifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                )
                            }
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

private val selectedStopKey = ActionParameters.Key<String>("selectedStop")
