package com.jsontextfield.departurescreen.wearapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.components.FilterChipStrip
import com.jsontextfield.departurescreen.core.ui.components.TripCodeBox
import com.jsontextfield.departurescreen.core.ui.navigation.NavigationActions
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.wearapp.R

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    navigationActions: NavigationActions,
) {
    val uiState by mainViewModel.uiState.collectAsState()
    MainScreen(
        uiState = uiState,
        onRetryClicked = mainViewModel::loadData,
        onRefresh = mainViewModel::refresh,
        onSetVisibleTrains = mainViewModel::setVisibleTrains,
        navigationActions = navigationActions,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainUIState,
    onRetryClicked: () -> Unit,
    onRefresh: () -> Unit,
    onSetVisibleTrains: (Set<String>) -> Unit,
    navigationActions: NavigationActions,
) {
    AppScaffold(timeText = {
        TimeText(backgroundColor = MaterialTheme.colorScheme.surface)
    }) {
        ScreenScaffold { innerPadding ->
            when (uiState.status) {
                Status.LOADING -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                Status.ERROR -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(stringResource(R.string.error))
                        Spacer(modifier = Modifier.height(8.dp))
                        FilledTonalButton(onClick = onRetryClicked) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }

                Status.LOADED -> {
                    val listState = rememberTransformingLazyColumnState()
                    val transformationSpec = rememberTransformationSpec()
                    val fontScale = LocalDensity.current.fontScale
                    Surface {
                        PullToRefreshBox(
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = onRefresh,
                        ) {
                            TransformingLazyColumn(
                                state = listState,
                                contentPadding = PaddingValues(
                                    bottom = 100.dp,
                                    top = innerPadding.calculateTopPadding()
                                )
                            ) {
                                item {
                                    if (uiState.status == Status.LOADED) {
                                        FilledTonalButton(
                                            onClick = navigationActions.onShowStations,
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                        ) {
                                            Text(
                                                text = uiState.selectedStation?.name.orEmpty(),
                                                modifier = Modifier.basicMarquee(),
                                            )
                                        }
                                    }
                                }
                                uiState.allTrips.distinctBy { it.code to it.name }.let { data ->
                                    if (data.size > 1) {
                                        item {
                                            FilterChipStrip(
                                                data = data.sortedBy { it.code },
                                                selectedItems = uiState.visibleTrains,
                                                onSelectionChanged = onSetVisibleTrains,
                                            )
                                        }
                                    }
                                }
                                itemsIndexed(
                                    items = uiState.allTrips.filter { it.isVisible },
                                    key = { _, trip -> trip.id },
                                ) { index, trip ->
                                    val minutesContentDescription = pluralStringResource(
                                        R.plurals.minutes_content_description,
                                        trip.departureDiffMinutes,
                                        trip.departureDiffMinutes,
                                    )
                                    ListHeader {
                                        Row(
                                            modifier = Modifier.semantics(mergeDescendants = true) {},
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            val shouldShowTrainCode = with(LocalDensity.current) {
                                                fontScale <= 1.5f
                                            }
                                            Column(
                                                modifier = Modifier
                                                    .weight(3 / 12f)
                                                    .semantics {
                                                        contentDescription = minutesContentDescription
                                                    },
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                            ) {
                                                Text(
                                                    text = trip.departureDiffMinutes.toString(),
                                                    modifier = Modifier.basicMarquee(),
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        textAlign = TextAlign.Center,
                                                        fontWeight = FontWeight.Bold,
                                                    ),
                                                    maxLines = 1,
                                                )
                                                Text(
                                                    text = stringResource(R.string.min),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    maxLines = 1,
                                                )
                                            }
                                            if (shouldShowTrainCode) {
                                                val fontScale = LocalDensity.current.fontScale
                                                TripCodeBox(
                                                    trip.code,
                                                    modifier = Modifier
                                                        .size((MaterialTheme.typography.titleSmall.fontSize.value * fontScale * 2).dp)
                                                        .background(color = trip.color, shape = SquircleShape)
                                                        .semantics {
                                                            contentDescription = if (trip.isBus) {
                                                                trip.code
                                                            } else {
                                                                trip.name
                                                            }
                                                        },
                                                )
                                            }
                                            Column(
                                                modifier = Modifier.weight(6 / 12f),
                                            ) {
                                                if (!shouldShowTrainCode) {
                                                    Text(
                                                        text = trip.name,
                                                        style = MaterialTheme.typography.labelSmall,
                                                    )
                                                }
                                                Text(
                                                    text = trip.destination,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis,
                                                    style = MaterialTheme.typography.titleSmall
                                                )
                                                if (trip.isCancelled) {
                                                    Text(
                                                        text = stringResource(R.string.cancelled),
                                                        color = MaterialTheme.colorScheme.error,
                                                        style = MaterialTheme.typography.labelMedium,
                                                    )
                                                } else if (trip.isExpress) {
                                                    Text(
                                                        text = stringResource(R.string.express),
                                                        color = MaterialTheme.colorScheme.primary,
                                                        style = MaterialTheme.typography.labelMedium,
                                                    )
                                                }
                                            }

                                            val platform = stringResource(R.string.platform, trip.platform)
                                            Text(
                                                text = trip.platform,
                                                maxLines = 3,
                                                modifier = Modifier
                                                    .weight(3 / 12f)
                                                    .clearAndSetSemantics {
                                                        if (trip.hasPlatform) {
                                                            contentDescription = platform
                                                        }
                                                    },
                                                textAlign = TextAlign.Center,
                                                style = if (trip.hasPlatform) {
                                                    MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary,
                                                    )
                                                } else {
                                                    MaterialTheme.typography.titleMedium
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}