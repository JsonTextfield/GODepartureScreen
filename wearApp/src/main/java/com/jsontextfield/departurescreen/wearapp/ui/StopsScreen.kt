
package com.jsontextfield.departurescreen.wearapp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import com.jsontextfield.departurescreen.core.entities.Stop
import com.jsontextfield.departurescreen.core.ui.components.ScrollToTopButton
import com.jsontextfield.departurescreen.core.ui.components.SearchBar
import com.jsontextfield.departurescreen.core.ui.components.StopListItem
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsViewModel
import kotlinx.coroutines.launch

@Composable
fun StopsScreen(
    stopsViewModel: StopsViewModel,
    onBackPressed: () -> Unit = {},
) {
    val uiState by stopsViewModel.uiState.collectAsState()
    StopsScreen(
        uiState = uiState,
        onStopSelected = {
            stopsViewModel.setSelectedStop(it)
            onBackPressed()
        },
        onFavouriteClick = stopsViewModel::setFavouriteStops,
        onBackPressed = onBackPressed,
    )
}

@Composable
fun StopsScreen(
    uiState: StopsUIState,
    onStopSelected: (Stop) -> Unit,
    onFavouriteClick: (Stop) -> Unit,
    onBackPressed: () -> Unit
) {
    val textFieldState = rememberTextFieldState()
    val gridState = rememberTransformingLazyColumnState()
    val scope = rememberCoroutineScope()
    BackHandler(onBack = onBackPressed)
    ScreenScaffold { innerPadding ->
        val filteredStops = uiState.getFilteredStops(textFieldState.text.toString())
        val density = LocalDensity.current
        val widthDp =
            (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues()
                .calculateLeftPadding(
                    LayoutDirection.Ltr
                ).value - WindowInsets.safeDrawing.asPaddingValues()
                .calculateRightPadding(LayoutDirection.Ltr).value).toInt()
        val columns = (widthDp / 600).coerceIn(1, 4)
            TransformingLazyColumn(
                state = gridState,
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .semantics {
                        collectionInfo = CollectionInfo(
                            rowCount = filteredStops.size,
                            columnCount = columns,
                        )
                    },
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    SearchBar(textFieldState)
                }
                itemsIndexed(
                    filteredStops,
                    key = { _, stop -> stop.code }) { index, stop ->
                    ListHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 40.dp)
                            .alpha(if (stop.isEnabled) 1f else 0.5f)
                            .background(
                                color =
                                    if (stop.code.split(",").any { it in uiState.selectedStop?.code.orEmpty() }) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else if (!stop.isEnabled) {
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    } else {
                                        MaterialTheme.colorScheme.background
                                    }
                            )
                            .clickable {
                                onStopSelected(stop)
                                onBackPressed()
                            }
                            .semantics {
                                collectionItemInfo = CollectionItemInfo(
                                    rowIndex = index / columns,
                                    columnIndex = index % columns,
                                    rowSpan = 1,
                                    columnSpan = 1,
                                )
                            }
                            .animateItem()
                    ) {
                        StopListItem(
                            stop = stop,
                            onFavouriteClick = { onFavouriteClick(stop) },
                        )
                    }
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), contentAlignment = Alignment.BottomCenter
    ) {
        Box(modifier = Modifier.size(24.dp)) {
            ScrollToTopButton(
                isVisible = gridState.anchorItemIndex > 4,
                onClick = {
                    scope.launch {
                        gridState.animateScrollToItem(0)
                    }
                }
            )
        }
    }
}
