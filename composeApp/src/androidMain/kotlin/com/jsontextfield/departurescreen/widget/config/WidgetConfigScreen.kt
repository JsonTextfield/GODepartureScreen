package com.jsontextfield.departurescreen.widget.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SortMode

@Composable
fun WidgetConfigScreen(
    widgetConfig: WidgetConfig,
    trips: List<Trip>,
    onSortModeChanged: (SortMode) -> Unit,
    onOpacityChanged: (Float) -> Unit,
    onStationButtonClicked: () -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit,
) {
    Scaffold { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(
                start = WindowInsets.safeContent.asPaddingValues().calculateStartPadding(
                    LayoutDirection.Ltr
                ),
                end = WindowInsets.safeContent.asPaddingValues().calculateEndPadding(
                    LayoutDirection.Ltr
                ),
            )
        ) {
            item {
                WidgetConfigPreview(
                    widgetConfig = widgetConfig,
                    trips = trips,
                    modifier = Modifier.aspectRatio(15 / 12f)
                )
            }
            item {
                WidgetConfigControls(
                    widgetConfig = widgetConfig,
                    onSortModeChanged = onSortModeChanged,
                    onOpacityChanged = onOpacityChanged,
                    onStationButtonClicked = onStationButtonClicked,
                    onCancel = onCancel,
                    onDone = onDone,
                )
            }
        }
    }
}