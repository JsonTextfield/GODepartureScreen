package com.jsontextfield.departurescreen.ui

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.entities.Alert
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.alerts
import departure_screen.composeapp.generated.resources.back
import departure_screen.composeapp.generated.resources.information_alerts
import departure_screen.composeapp.generated.resources.service_alerts
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    informationAlerts: List<Alert>,
    serviceAlerts: List<Alert>,
    onBackPressed: () -> Unit = {},
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
) {
    Back(onBackPressed)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.alerts),
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                modifier = Modifier.shadow(4.dp)
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
            )
        ) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = WindowInsets.safeDrawing.asPaddingValues().calculateLeftPadding(LayoutDirection.Ltr),
                        end = WindowInsets.safeDrawing.asPaddingValues().calculateRightPadding(LayoutDirection.Ltr),
                    ).semantics {
                        collectionInfo = CollectionInfo(
                            rowCount = informationAlerts.size + serviceAlerts.size,
                            columnCount = 1,
                        )
                    },
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp) + PaddingValues(
                    bottom = 100.dp,
                ),
                columns = GridCells.Adaptive(240.dp),
            ) {
                if (serviceAlerts.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            stringResource(Res.string.service_alerts),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.semantics {
                                heading()
                            },
                        )
                    }
                    itemsIndexed(serviceAlerts, key = { _, item -> item.id }) { index, alert ->
                        AlertItem(
                            alert,
                            modifier = Modifier.semantics {
                                collectionItemInfo = CollectionItemInfo(
                                    rowIndex = index,
                                    columnIndex = 0,
                                    rowSpan = 1,
                                    columnSpan = 1,
                                )
                            }.animateItem()
                        )
                    }
                }
                if (informationAlerts.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            if (serviceAlerts.isNotEmpty()) {
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
                    itemsIndexed(informationAlerts, key = { _, item -> item.id }) { index, alert ->
                        AlertItem(
                            alert,
                            modifier = Modifier.semantics {
                                collectionItemInfo = CollectionItemInfo(
                                    rowIndex = serviceAlerts.size + index,
                                    columnIndex = 0,
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

private operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    return PaddingValues(
        start = this.calculateStartPadding(LayoutDirection.Ltr) + other.calculateStartPadding(LayoutDirection.Ltr),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(LayoutDirection.Ltr) + other.calculateEndPadding(LayoutDirection.Ltr),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}

@Composable
expect fun Back(onBackPressed: () -> Unit)