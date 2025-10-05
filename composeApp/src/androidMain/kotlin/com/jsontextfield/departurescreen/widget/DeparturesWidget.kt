package com.jsontextfield.departurescreen.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import com.jsontextfield.departurescreen.core.ui.viewmodels.WidgetUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.WidgetViewModel
import kotlinx.datetime.Instant
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.minutes

private class AppWidgetViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore = ViewModelStore()
}

object WidgetViewModelStore {
    private val owners = mutableMapOf<GlanceId, ViewModelStoreOwner>()

    fun get(glanceId: GlanceId): ViewModelStoreOwner {
        return owners.getOrPut(glanceId) {
            AppWidgetViewModelStoreOwner()
        }
    }

    // Optional: Clean up the store when a widget is deleted
    fun remove(glanceId: GlanceId) {
        owners.remove(glanceId)?.viewModelStore?.clear()
    }
}

class DeparturesWidget : GlanceAppWidget() {

    override suspend fun providePreview(context: Context, widgetCategory: Int) {
        val uiState = WidgetUIState(
            status = Status.LOADED,
            selectedStation = CombinedStation(
                name = "Union Station GO",
                codes = listOf("UN"),
                types = listOf("Train Station"),
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
                    platform = "9 & 10",
                    departureTime = Instant.fromEpochMilliseconds(16.minutes.inWholeMilliseconds),
                    lastUpdated = Instant.fromEpochMilliseconds(0),
                )
            )
        )

        provideContent {
            MyContent(uiState)
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            val viewModelStoreOwner = WidgetViewModelStore.get(id)
            val viewModel: WidgetViewModel = koinViewModel(
                viewModelStoreOwner = viewModelStoreOwner
            )
            val uiState by viewModel.uiState.collectAsState()
            MyContent(uiState, viewModel::refresh)
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        // Clean up the ViewModelStoreOwner when the widget is deleted
        WidgetViewModelStore.remove(glanceId)
    }
}
