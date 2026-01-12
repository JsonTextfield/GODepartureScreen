package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.components.AlertFilterChipStrip
import com.jsontextfield.departurescreen.core.ui.components.AlertItem
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.components.ErrorScreen
import com.jsontextfield.departurescreen.core.ui.components.LoadingScreen
import com.jsontextfield.departurescreen.core.ui.components.ScrollToTopButton
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.alerts
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.ExperimentalUuidApi


@Composable
fun AlertsScreen(
    alertsViewModel: AlertsViewModel,
    onBackPressed: () -> Unit = {},
) {
    val uiState by alertsViewModel.uiState.collectAsState()
    AlertsScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onRefresh = alertsViewModel::refresh,
        onRetryClicked = alertsViewModel::loadData,
        onLinesSelected = alertsViewModel::setFilter,
        onReadAlert = alertsViewModel::readAlert,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun AlertsScreen(
    uiState: AlertsUIState,
    onRetryClicked: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onReadAlert: (String) -> Unit = {},
    onLinesSelected: (Set<String>, Boolean) -> Unit = { _, _ -> },
) {
    val scope = rememberCoroutineScope()
    val gridState = rememberLazyStaggeredGridState()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(onBackPressed) },
                title = {
                    Text(
                        text = stringResource(Res.string.alerts),
                        modifier = Modifier.semantics { heading() }
                    )
                },
                modifier = Modifier.shadow(4.dp)
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
                    isVisible = gridState.firstVisibleItemIndex > 4,
                    onClick = {
                        scope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    }
                )
            }
        },
    ) { innerPadding ->
        when (uiState.status) {
            Status.LOADING -> {
                LoadingScreen()
            }

            Status.ERROR -> {
                ErrorScreen(onRetry = onRetryClicked)
            }

            Status.LOADED -> {
                val density = LocalDensity.current
                val widthDp =
                    (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues()
                        .calculateLeftPadding(
                            LayoutDirection.Ltr
                        ).value - WindowInsets.safeDrawing.asPaddingValues()
                        .calculateRightPadding(LayoutDirection.Ltr).value).toInt()
                val columns = (widthDp / 320).coerceAtLeast(1)

                val visibleItems by remember {
                    derivedStateOf {
                        gridState.layoutInfo.visibleItemsInfo
                    }
                }
                LaunchedEffect(visibleItems) {
                    visibleItems.forEach { item ->
                        val alert = if (item.key in uiState.alerts.map { it.id }) {
                            uiState.alerts.firstOrNull { it.id == item.key }
                        } else {
                            null
                        }
                        if (alert?.isRead == false) {
                            onReadAlert(alert.id)
                        }
                    }
                }
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                ) {
                    Column {
                        AlertFilterChipStrip(
                            data = uiState.allLines,
                            selectedItems = uiState.selectedLines,
                            onSelectionChanged = onLinesSelected,
                            isUnreadSelected = uiState.isUnreadSelected,
                        )
                        LazyVerticalStaggeredGrid(
                            state = gridState,
                            modifier = Modifier
                                .fillMaxSize()
                                .semantics {
                                    collectionInfo = CollectionInfo(
                                        rowCount = uiState.alerts.size,
                                        columnCount = columns,
                                    )
                                },
                            verticalItemSpacing = 8.dp,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(
                                top = 16.dp,
                                start = WindowInsets.safeDrawing.asPaddingValues()
                                    .calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                                end = WindowInsets.safeDrawing.asPaddingValues()
                                    .calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                                bottom = 100.dp,
                            ),
                            columns = StaggeredGridCells.Fixed(columns),
                        ) {
                            itemsIndexed(
                                items = uiState.alerts,
                                span = { _, _ -> StaggeredGridItemSpan.SingleLane },
                                key = { _, item -> item.id },
                                contentType = { _, _ -> Alert::class },
                            ) { index, alert ->
                                AlertItem(
                                    alert,
                                    modifier = Modifier.semantics {
                                        collectionItemInfo = CollectionItemInfo(
                                            rowIndex = index / columns,
                                            columnIndex = index % columns,
                                            rowSpan = 1,
                                            columnSpan = 1,
                                        )
                                    }.animateItem()
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}