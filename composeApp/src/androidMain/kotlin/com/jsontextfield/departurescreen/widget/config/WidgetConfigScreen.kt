package com.jsontextfield.departurescreen.widget.config

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.SortMode

@Composable
fun WidgetConfigScreen(
    widgetConfig: WidgetConfig,
    onSortModeChanged: (SortMode) -> Unit,
    onOpacityChanged: (Float) -> Unit,
    onStopButtonClicked: () -> Unit,
    onCancel: () -> Unit,
    onDone: () -> Unit,
) {
    Scaffold { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(300.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                start = WindowInsets.safeDrawing.asPaddingValues().calculateStartPadding(
                    LayoutDirection.Ltr
                ) + 16.dp,
                end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(
                    LayoutDirection.Ltr
                ) + 16.dp,
            )
        ) {
            item {
                WidgetConfigPreview(
                    widgetConfig = widgetConfig,
                    modifier = Modifier.aspectRatio(15 / 12f)
                )
            }
            item {
                WidgetConfigControls(
                    widgetConfig = widgetConfig,
                    onSortModeChanged = onSortModeChanged,
                    onOpacityChanged = onOpacityChanged,
                    onStopButtonClicked = onStopButtonClicked,
                    onCancel = onCancel,
                    onDone = onDone,
                )
            }
        }
    }
}