package com.jsontextfield.departurescreen.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.entities.Station
import com.jsontextfield.departurescreen.ui.menu.Action
import com.jsontextfield.departurescreen.ui.menu.ActionBar
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.error
import departure_screen.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: UIState,
    timeRemaining: Int,
    actions: List<Action>,
    onRetryClicked: () -> Unit,
    onRefresh: () -> Unit,
    onSetVisibleTrains: (Set<String>) -> Unit,
    onStationSelected: (Station) -> Unit,
) {
    Scaffold(
        topBar = {
            if (uiState.status == Status.LOADED) {
                TopAppBar(
                    title = {
                        StationComboBox(
                            items = uiState.allStations,
                            selectedItem = uiState.selectedStation,
                            onItemSelected = onStationSelected
                        )
                    },
                    actions = {
                        val screenWidthDp = (LocalWindowInfo.current.containerSize.width / LocalDensity.current.density)
                        val maxActions = when {
                            screenWidthDp < 400 -> screenWidthDp / 4 / 48
                            screenWidthDp < 600 -> screenWidthDp / 3 / 48
                            screenWidthDp < 800 -> screenWidthDp / 2 / 48
                            else -> screenWidthDp * 2 / 3 / 48
                        }
                        ActionBar(
                            maxActions = maxActions.toInt() + 1,
                            actions = actions,
                        )
                    },
                    modifier = Modifier.shadow(4.dp)
                )
            }

        },
        floatingActionButton = {
            if (uiState.status == Status.LOADED) {
                CountdownTimer(timeRemaining)
            }
        },
    ) { innerPadding ->
        when (uiState.status) {
            Status.LOADING -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            Status.ERROR -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(Res.string.error))
                    Spacer(modifier = Modifier.height(8.dp))
                    FilledTonalButton(onClick = onRetryClicked) {
                        Text(stringResource(Res.string.retry))
                    }
                }
            }

            Status.LOADED -> {
                Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                    uiState.allTrains.distinctBy { it.code to it.name }.let { data ->
                        AnimatedVisibility(data.size > 1) {
                            FilterChipStrip(
                                data = data.sortedBy { it.code },
                                selectedItems = uiState.visibleTrains,
                                onSelectionChanged = onSetVisibleTrains
                            )
                        }
                    }
                    Surface {
                        PullToRefreshBox(
                            isRefreshing = uiState.isRefreshing,
                            onRefresh = onRefresh,
                        ) {
                            TrainList(trains = uiState.allTrains.filter { it.isVisible })
                        }
                    }
                }
            }
        }
    }
}