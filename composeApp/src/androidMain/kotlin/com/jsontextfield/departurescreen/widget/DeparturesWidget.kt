package com.jsontextfield.departurescreen.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.material3.ColorProviders
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
import kotlinx.datetime.Instant
import org.koin.java.KoinJavaComponent.inject
import kotlin.time.Duration.Companion.minutes

class DeparturesWidget : GlanceAppWidget() {

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
            GlanceTheme(
                colors = ColorProviders(
                    light = lightScheme,
                    dark = darkScheme,
                )
            ) {
                MainContent(uiState)
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
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

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            val uiState by viewModel.uiState.collectAsState()
            GlanceTheme(
                colors = ColorProviders(
                    light = lightScheme,
                    dark = darkScheme,
                )
            ) {
                MainContent(uiState, viewModel::refresh)
            }
        }
    }
}
