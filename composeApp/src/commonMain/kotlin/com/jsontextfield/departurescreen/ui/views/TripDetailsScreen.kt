@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.components.ErrorScreen
import com.jsontextfield.departurescreen.core.ui.components.LoadingScreen
import com.jsontextfield.departurescreen.core.ui.components.TripCodeBox
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import com.jsontextfield.departurescreen.core.ui.viewmodels.TripDetailsViewModel
import com.jsontextfield.departurescreen.ui.views.tripdetails.AlertsSection
import com.jsontextfield.departurescreen.ui.views.tripdetails.MoreTripsSection
import com.jsontextfield.departurescreen.ui.views.tripdetails.SectionHeader
import com.jsontextfield.departurescreen.ui.views.tripdetails.StopsSection
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.more_trips
import org.jetbrains.compose.resources.stringResource

@Composable
fun TripDetailsScreen(
    tripDetailsViewModel: TripDetailsViewModel,
    onBackPressed: () -> Unit,
    onTripSelected: (Trip) -> Unit,
) {
    val uiState by tripDetailsViewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TripCodeBox(
                            tripCode = uiState.lineCode,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = lineColours[uiState.lineCode] ?: Color.Gray,
                                    shape = SquircleShape
                                )
                        )
                        Text(text = uiState.destination)
                    }
                },
                navigationIcon = {
                    BackButton(onBackPressed = onBackPressed)
                },
                modifier = Modifier.shadow(4.dp)
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(top = padding.calculateTopPadding())) {
            when (uiState.status) {
                Status.LOADING -> LoadingScreen()
                Status.ERROR -> ErrorScreen(onRetry = { tripDetailsViewModel.loadData() })
                Status.LOADED -> {
                    val density = LocalDensity.current
                    val widthDp = (LocalWindowInfo.current.containerSize.width / density.density).toInt()
                    val columns = (widthDp / 320).coerceIn(1, 4)
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        columns = StaggeredGridCells.Fixed(columns),
                        contentPadding = PaddingValues(
                            start = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                            bottom = 100.dp,
                        )
                    ) {
                        if (uiState.alerts.isNotEmpty()) {
                            item {
                                AlertsSection(uiState.alerts, modifier = Modifier.animateItem())
                            }
                        }
                        if (uiState.moreTrips.isNotEmpty()) {
                            item {
                                MoreTripsSection(
                                    moreTrips = uiState.moreTrips,
                                    title = stringResource(Res.string.more_trips, uiState.selectedStop),
                                    timeFormat = uiState.timeFormat,
                                    onTripSelected = onTripSelected,
                                    modifier = Modifier.animateItem(),
                                )
                            }
                        }
                        if (uiState.serviceGuarantee.isNotEmpty()) {
                            item {
                                Column(modifier = Modifier.animateItem()) {
                                    SectionHeader("Service Guarantee")
                                    Text(
                                        text = uiState.serviceGuarantee,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        if (uiState.stops.isNotEmpty()) {
                            item {
                                StopsSection(
                                    stops = uiState.stops,
                                    timeFormat = uiState.timeFormat,
                                    selectedStop = uiState.selectedStop,
                                    onStopSelected = { stopName ->
                                        tripDetailsViewModel.setSelectedStop(stopName)
                                        onBackPressed()
                                    },
                                    modifier = Modifier.animateItem(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}