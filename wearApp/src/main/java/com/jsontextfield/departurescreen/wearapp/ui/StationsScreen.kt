@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.wear.compose.material3.ScreenScaffold
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.ui.components.ScrollToTopButton
import com.jsontextfield.departurescreen.core.ui.components.SearchBar
import com.jsontextfield.departurescreen.core.ui.components.StationListItem
import com.jsontextfield.departurescreen.core.ui.viewmodels.StationsUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.StationsViewModel
import kotlinx.coroutines.launch

@Composable
fun StationsScreen(
    stationsViewModel: StationsViewModel,
    onBackPressed: () -> Unit = {},
) {
    val uiState by stationsViewModel.uiState.collectAsState()
    StationsScreen(
        uiState = uiState,
        onStationSelected = {
            stationsViewModel.setSelectedStation(it)
            onBackPressed()
        },
        onFavouriteClick = stationsViewModel::setFavouriteStations,
        onBackPressed = onBackPressed,
    )
}

@Composable
fun StationsScreen(
    uiState: StationsUIState,
    onStationSelected: (CombinedStation) -> Unit,
    onFavouriteClick: (CombinedStation) -> Unit,
    onBackPressed: () -> Unit
) {
    val textFieldState = rememberTextFieldState()
    val gridState = rememberTransformingLazyColumnState()
    val scope = rememberCoroutineScope()
    BackHandler(onBack = onBackPressed)
    ScreenScaffold { innerPadding ->
        val filteredStations = uiState.getFilteredStations(textFieldState.text.toString())
        val density = LocalDensity.current
        val widthDp =
            (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues()
                .calculateLeftPadding(
                    LayoutDirection.Ltr
                ).value - WindowInsets.safeDrawing.asPaddingValues()
                .calculateRightPadding(LayoutDirection.Ltr).value).toInt()
        val columns = (widthDp / 600).coerceIn(1, 4)
        Surface {
            TransformingLazyColumn(
                state = gridState,
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .semantics {
                        collectionInfo = CollectionInfo(
                            rowCount = filteredStations.size,
                            columnCount = columns,
                        )
                    },
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    SearchBar(textFieldState)
                }
                itemsIndexed(
                    filteredStations,
                    key = { _, station -> station.codes }) { index, station ->
                    ListHeader(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 40.dp)
                            .alpha(if (station.isEnabled) 1f else 0.5f)
                            .background(
                                color =
                                    if (station.codes.any { it in uiState.selectedStation?.codes.orEmpty() }) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else if (!station.isEnabled) {
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                            )
                            .clickable {
                                onStationSelected(station)
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
                        StationListItem(
                            station = station,
                            onFavouriteClick = { onFavouriteClick(station) },
                        )
                    }
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
