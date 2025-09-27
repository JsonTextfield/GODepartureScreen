@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import com.jsontextfield.departurescreen.core.ui.UIState
import com.jsontextfield.departurescreen.core.ui.components.ScrollToTopButton
import com.jsontextfield.departurescreen.core.ui.components.SearchBar
import com.jsontextfield.departurescreen.core.ui.components.StationListItem
import com.jsontextfield.departurescreen.ui.components.BackButton
import kotlinx.coroutines.launch

@Composable
fun StationsScreen(
    uiState: UIState,
    onStationSelected: (CombinedStation) -> Unit,
    onFavouriteClick: (CombinedStation) -> Unit,
    onBackPressed: () -> Unit
) {
    val textFieldState = rememberTextFieldState()
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(onBackPressed) },
                title = { SearchBar(textFieldState) },
                modifier = Modifier.shadow(4.dp),
            )
        },
        floatingActionButton = {
            ScrollToTopButton(
                isVisible = gridState.firstVisibleItemIndex > 10,
                onClick = {
                    scope.launch {
                        gridState.animateScrollToItem(0)
                    }
                }
            )
        },
    ) { innerPadding ->
        val filteredStations = uiState.getFilteredStations(textFieldState.text.toString())
        val density = LocalDensity.current
        val widthDp = (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues().calculateLeftPadding(
            LayoutDirection.Ltr).value - WindowInsets.safeDrawing.asPaddingValues().calculateRightPadding(LayoutDirection.Ltr).value).toInt()
        val columns = (widthDp / 600).coerceIn(1, 4)
        LazyVerticalGrid(
            state = gridState,
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .semantics {
                    collectionInfo = CollectionInfo(
                        rowCount = filteredStations.size,
                        columnCount = columns,
                    )
                },
            columns = GridCells.Adaptive(300.dp)
        ) {
            itemsIndexed(
                filteredStations,
                key = { _, station -> station.codes }) { index, station ->
                StationListItem(
                    station = station,
                    onFavouriteClick = { onFavouriteClick(station) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
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
                        .padding(8.dp)
                        .padding(
                            start = WindowInsets.safeDrawing.asPaddingValues().calculateStartPadding(
                                LayoutDirection.Ltr
                            ),
                            end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(
                                LayoutDirection.Ltr
                            ),
                        ).semantics {
                            collectionItemInfo = CollectionItemInfo(
                                rowIndex = index / columns,
                                columnIndex = index % columns,
                                rowSpan = 1,
                                columnSpan = 1,
                            )
                        }.animateItem()
                )
            }
        }
    }
}
