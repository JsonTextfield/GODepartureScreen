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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.components.CountdownTimer
import com.jsontextfield.departurescreen.core.ui.components.ErrorScreen
import com.jsontextfield.departurescreen.core.ui.components.LoadingScreen
import com.jsontextfield.departurescreen.core.ui.components.TrainList
import com.jsontextfield.departurescreen.core.ui.components.TripFilterChipStrip
import com.jsontextfield.departurescreen.core.ui.menu.Action
import com.jsontextfield.departurescreen.core.ui.menu.ActionBar
import com.jsontextfield.departurescreen.core.ui.menu.getActions
import com.jsontextfield.departurescreen.core.ui.navigation.NavigationActions
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainUIState
import com.jsontextfield.departurescreen.core.ui.viewmodels.MainViewModel


@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    navigationActions: NavigationActions,
) {
    val uiState by mainViewModel.uiState.collectAsState()
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()
    MainScreen(
        uiState = uiState,
        timeRemaining = timeRemaining,
        actions = getActions(mainViewModel, navigationActions),
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
    timeRemaining: Int,
    actions: List<Action>,
    onRetryClicked: () -> Unit,
    onRefresh: () -> Unit,
    onSetVisibleTrains: (Set<String>) -> Unit,
    navigationActions: NavigationActions,
) {
    Scaffold(
        topBar = {
            if (uiState.status == Status.LOADED) {
                TopAppBar(
                    title = {
                        FilledTonalButton(
                            onClick = navigationActions.onShowStations,
                            contentPadding = PaddingValues(horizontal = 8.dp),
                        ) {
                            Text(
                                text = uiState.selectedStation?.name.orEmpty(),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(11 / 12f).basicMarquee(),
                            )
                        }
                    },
                    actions = {
                        val maxActions = with(LocalDensity.current) {
                            val screenWidthDp = LocalWindowInfo.current.containerSize.width.toDp()
                            val fraction = when {
                                screenWidthDp.value < 400 -> 1 / 4f
                                screenWidthDp.value < 600 -> 1 / 3f
                                screenWidthDp.value < 800 -> 1 / 2f
                                else -> 2 / 3f
                            }
                            (screenWidthDp.value * fraction / 48f).toInt()
                        }
                        ActionBar(
                            maxActions = if (actions.size - maxActions == 1) {
                                maxActions + 1
                            } else {
                                maxActions
                            },
                            actions = actions,
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
                Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                    uiState.allTrips.distinctBy { it.code to it.name }.let { data ->
                        AnimatedVisibility(data.size > 1) {
                            TripFilterChipStrip(
                                data = data.sortedBy { it.code },
                                selectedItems = uiState.visibleTrains,
                                onSelectionChanged = onSetVisibleTrains,
                            )
                        }
                    }
                    Surface {
                        PullToRefreshBox(
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = onRefresh,
                        ) {
                            TrainList(trips = uiState.allTrips.filter { it.isVisible })
                        }
                    }
                }
            }
        }
    }
}