package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.components.AlertItem
import com.jsontextfield.departurescreen.core.ui.components.ErrorScreen
import com.jsontextfield.departurescreen.core.ui.components.LoadingScreen
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.AlertsViewModel
import com.jsontextfield.departurescreen.ui.components.BackButton
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.alerts
import departure_screen.composeapp.generated.resources.information_alerts
import departure_screen.composeapp.generated.resources.service_alerts
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
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun AlertsScreen(
    uiState: AlertsUIState,
    onRetryClicked: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    val density = LocalDensity.current
    val widthDp =
        (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues()
            .calculateLeftPadding(
                LayoutDirection.Ltr
            ).value - WindowInsets.safeDrawing.asPaddingValues()
            .calculateRightPadding(LayoutDirection.Ltr).value).toInt()
    val columns = (widthDp / 600).coerceIn(1, 4)
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
    ) { innerPadding ->
        when (uiState.status) {
            Status.LOADING -> {
                LoadingScreen()
            }

            Status.ERROR -> {
                ErrorScreen(onRetry = onRetryClicked)
            }

            Status.LOADED -> {
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                ) {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics {
                                collectionInfo = CollectionInfo(
                                    rowCount = uiState.informationAlerts.size + uiState.serviceAlerts.size,
                                    columnCount = columns,
                                )
                            },
                        verticalItemSpacing = 16.dp,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            start = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                            bottom = 100.dp
                        ),
                        columns = StaggeredGridCells.Adaptive(240.dp),
                    ) {
                        if (uiState.serviceAlerts.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Text(
                                    stringResource(Res.string.service_alerts),
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.semantics {
                                        heading()
                                    },
                                )
                            }
                            itemsIndexed(uiState.serviceAlerts, key = { _, item -> item.id }) { index, alert ->
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
                        }
                        if (uiState.informationAlerts.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Column {
                                    if (uiState.serviceAlerts.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                    Text(
                                        stringResource(Res.string.information_alerts),
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.semantics {
                                            heading()
                                        },
                                    )
                                }
                            }
                            itemsIndexed(uiState.informationAlerts, key = { _, item -> item.id }) { index, alert ->
                                AlertItem(
                                    alert,
                                    modifier = Modifier.semantics {
                                        collectionItemInfo = CollectionItemInfo(
                                            rowIndex = (uiState.serviceAlerts.size + index) / columns,
                                            columnIndex = (uiState.serviceAlerts.size + index) % columns,
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
    }
}