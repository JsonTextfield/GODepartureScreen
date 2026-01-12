@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.jsontextfield.departurescreen.core.entities.Stop
import com.jsontextfield.departurescreen.core.ui.StopType
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.components.ScrollToTopButton
import com.jsontextfield.departurescreen.core.ui.components.SearchBar
import com.jsontextfield.departurescreen.core.ui.components.StopFilterChipStrip
import com.jsontextfield.departurescreen.core.ui.components.StopListItem
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.StopsViewModel
import kotlinx.coroutines.launch

@Composable
fun StopsScreen(
    stopsViewModel: StopsViewModel,
    onBackPressed: () -> Unit = {},
) {
    StopsScreen(
        stopsViewModel = stopsViewModel,
        onStopSelected = {
            stopsViewModel.setSelectedStop(it)
            onBackPressed()
        },
        onBackPressed = onBackPressed,
    )
}

@Composable
fun StopsScreen(
    stopsViewModel: StopsViewModel,
    onStopSelected: (Stop) -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    val uiState by stopsViewModel.uiState.collectAsState()
    StopsScreen(
        uiState = uiState,
        onStopSelected = onStopSelected,
        onFavouriteClick = stopsViewModel::setFavouriteStops,
        onBackPressed = onBackPressed,
        onSetStopType = stopsViewModel::setStopType,
    )
}


@Composable
private fun StopsScreen(
    uiState: StopsUIState,
    onStopSelected: (Stop) -> Unit,
    onFavouriteClick: (Stop) -> Unit,
    onBackPressed: () -> Unit,
    onSetStopType: (StopType?) -> Unit,
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
            Box(
                modifier = Modifier.padding(
                    end = TopAppBarDefaults
                        .windowInsets
                        .asPaddingValues()
                        .calculateEndPadding(LayoutDirection.Ltr),
                ),
            ) {
                ScrollToTopButton(
                    isVisible = gridState.firstVisibleItemIndex > 10,
                    onClick = {
                        scope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    }
                )
            }
        },
    ) { innerPadding ->

        Surface(Modifier.padding(top = innerPadding.calculateTopPadding())) {
            Column {
                val filteredStops = uiState.getFilteredStops(textFieldState.text.toString())
                val density = LocalDensity.current
                val widthDp =
                    (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues()
                        .calculateLeftPadding(
                            LayoutDirection.Ltr
                        ).value - WindowInsets.safeDrawing.asPaddingValues()
                        .calculateRightPadding(LayoutDirection.Ltr).value).toInt()
                val columns = (widthDp / 300).coerceIn(1, 4)
                StopFilterChipStrip(uiState.stopType, onSetStopType)

                LazyVerticalGrid(
                    state = gridState,
                    modifier = Modifier
                        .semantics {
                            collectionInfo = CollectionInfo(
                                rowCount = filteredStops.size,
                                columnCount = columns,
                            )
                        },
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(bottom = 100.dp),
                ) {
                    itemsIndexed(
                        filteredStops,
                        key = { _, stop -> stop.code.split(",") }) { index, stop ->
                        StopListItem(
                            stop = stop,
                            onFavouriteClick = { onFavouriteClick(stop) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp)
                                .alpha(if (stop.isEnabled) 1f else 0.5f)
                                .background(
                                    color =
                                        if (stop.code.split(",")
                                                .any { it in uiState.selectedStop?.code?.split(",").orEmpty() }
                                        ) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else if (!stop.isEnabled) {
                                            MaterialTheme.colorScheme.surfaceContainerLow
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        }
                                )
                                .clickable {
                                    onStopSelected(stop)
                                }
                                .padding(8.dp)
                                .then(
                                    if (index % columns == columns - 1) {
                                        Modifier
                                            .padding(
                                                end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(
                                                    LayoutDirection.Ltr
                                                )
                                            )
                                    } else if (index % columns == 0) {
                                        Modifier
                                            .padding(
                                                start = WindowInsets.safeDrawing.asPaddingValues()
                                                    .calculateStartPadding(
                                                        LayoutDirection.Ltr
                                                    )
                                            )
                                    } else {
                                        Modifier
                                    }
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
    }
}
