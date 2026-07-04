package com.jsontextfield.departurescreen.ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.components.CountdownTimer
import com.jsontextfield.departurescreen.core.ui.components.ErrorScreen
import com.jsontextfield.departurescreen.core.ui.components.LoadingScreen
import com.jsontextfield.departurescreen.core.ui.components.TripFilterChipStrip
import com.jsontextfield.departurescreen.core.ui.components.TripList
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel
import com.jsontextfield.departurescreen.ui.intents.MainScreenAction
import com.jsontextfield.departurescreen.ui.intents.MainScreenNavigationAction
import com.jsontextfield.departurescreen.ui.intents.Refresh
import com.jsontextfield.departurescreen.ui.intents.Retry
import com.jsontextfield.departurescreen.ui.intents.SetFavouriteStop
import com.jsontextfield.departurescreen.ui.intents.SetSortMode
import com.jsontextfield.departurescreen.ui.intents.SetVisibleTrains
import com.jsontextfield.departurescreen.ui.intents.Stops
import com.jsontextfield.departurescreen.ui.intents.TripDetails
import com.jsontextfield.departurescreen.ui.menu.ActionBar


@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    onNavigationAction: (MainScreenNavigationAction) -> Unit = {},
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()
    MainScreen(
        uiState = uiState,
        timeRemaining = timeRemaining,
        onRetryClicked = mainViewModel::loadData,
        onRefresh = mainViewModel::refresh,
        onSetVisibleTrains = mainViewModel::setVisibleTrains,
        onAction = { action ->
            when (action) {
                Refresh -> mainViewModel.refresh()
                Retry -> mainViewModel.loadData()
                is SetFavouriteStop -> mainViewModel.setFavouriteStops(action.stop)
                is SetSortMode -> mainViewModel.setSortMode(action.sortMode)
                is SetVisibleTrains -> mainViewModel.setVisibleTrains(action.visibleTrains)
                else -> {
                    if (action is MainScreenNavigationAction) {
                        onNavigationAction(action)
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainUIState,
    timeRemaining: Int,
    onRetryClicked: () -> Unit,
    onRefresh: () -> Unit,
    onSetVisibleTrains: (Set<String>) -> Unit,
    onAction: (MainScreenAction) -> Unit = {},
) {
    Scaffold(
        topBar = {
            if (uiState.status == Status.LOADED) {
                TopAppBar(
                    title = {
                        FilledTonalButton(
                            onClick = { onAction(Stops()) },
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        ) {
                            Text(
                                text = uiState.selectedStop?.name.orEmpty(),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .widthIn(max = 400.dp)
                                    .fillMaxWidth(11 / 12f)
                                    .basicMarquee(),
                            )
                        }
                    },
                    actions = {
                        ActionBar(
                            uiState = uiState,
                            onActionClicked = onAction,
                        )
                    },
                    modifier = Modifier.shadow(4.dp)
                )
            }

        },
        floatingActionButton = {
            if (uiState.status == Status.LOADED) {
                Box(
                    modifier = Modifier.padding(
                        end = TopAppBarDefaults
                            .windowInsets
                            .asPaddingValues()
                            .calculateEndPadding(LayoutDirection.Ltr),
                    ),
                ) {
                    CountdownTimer(timeRemaining)
                }
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
                Surface {
                    val pullToRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = onRefresh,
                        state = pullToRefreshState,
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                        indicator = {
                            PullToRefreshDefaults.Indicator(
                                state = pullToRefreshState,
                                isRefreshing = uiState.isRefreshing,
                                modifier = Modifier.align(Alignment.TopCenter),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    ) {
                        Column {
                            uiState.allTrips.distinctBy { it.code to it.name }.let { data ->
                                AnimatedVisibility(data.size > 1) {
                                    TripFilterChipStrip(
                                        data = data.sortedBy { it.code },
                                        selectedItems = uiState.visibleTrains,
                                        onSelectionChanged = onSetVisibleTrains,
                                    )
                                }
                            }
                            TripList(
                                trips = uiState.allTrips.filter { it.isVisible },
                                timeFormat = uiState.timeFormat,
                                onItemClick = { onAction(TripDetails(it)) },
                            )
                        }
                    }
                }
            }
        }
    }
}