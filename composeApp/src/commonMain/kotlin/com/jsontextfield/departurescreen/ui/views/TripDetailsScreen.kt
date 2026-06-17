@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.components.AlertItem
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.components.ErrorScreen
import com.jsontextfield.departurescreen.core.ui.components.LoadingScreen
import com.jsontextfield.departurescreen.core.ui.components.TripCodeBox
import com.jsontextfield.departurescreen.core.ui.components.TripDetailStopListHeader
import com.jsontextfield.departurescreen.core.ui.components.TripDetailStopListItem
import com.jsontextfield.departurescreen.core.ui.components.TripListHeader
import com.jsontextfield.departurescreen.core.ui.components.TripListItem
import com.jsontextfield.departurescreen.core.ui.components.isEven
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import com.jsontextfield.departurescreen.core.ui.viewmodels.TripDetailsViewModel
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.alerts
import departure_screen.composeapp.generated.resources.more_trips
import departure_screen.composeapp.generated.resources.stops
import org.jetbrains.compose.resources.stringResource

@Composable
fun TripDetailsScreen(
    tripDetailsViewModel: TripDetailsViewModel,
    onBackPressed: () -> Unit,
    onTripSelected: (Trip) -> Unit,
) {
    val uiState by tripDetailsViewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            start = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                            end = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                            bottom = 100.dp,
                        )
                    ) {
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
                                SectionHeader(stringResource(Res.string.stops))
                            }
                            item {
                                Column(
                                    modifier = Modifier
                                        .widthIn(max = 400.dp)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = .5f),
                                            RoundedCornerShape(8.dp)
                                        ).animateItem()
                                ) {
                                    TripDetailStopListHeader(
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                    )
                                    uiState.stops.forEachIndexed { index, stop ->
                                        Surface(
                                            tonalElevation = if (index.isEven) 1.dp else 0.dp,
                                        ) {
                                            TripDetailStopListItem(
                                                stop = stop,
                                                timeFormat = uiState.timeFormat,
                                                isSelected = stop.name == uiState.selectedStop,
                                                isEnabled = index >= uiState.stops.indexOfFirst { it.name == uiState.selectedStop },
                                                modifier = Modifier
                                                    .heightIn(min = 60.dp)
                                                    .clickable(onClick = {
                                                        tripDetailsViewModel.setSelectedStop(stop.code)
                                                        onBackPressed()
                                                    })
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        if (uiState.moreTrips.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    stringResource(
                                        Res.string.more_trips,
                                        uiState.selectedStop
                                    )
                                )
                            }
                            item {
                                Column(
                                    modifier = Modifier
                                        .widthIn(max = 400.dp)
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = .5f),
                                            RoundedCornerShape(8.dp)
                                        ).animateItem()
                                ) {
                                    TripListHeader(
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        )
                                    )
                                    uiState.moreTrips.forEachIndexed { index, trip ->
                                        Surface(
                                            tonalElevation = if (index.isEven) 1.dp else 0.dp,
                                        ) {
                                            TripListItem(
                                                trip = trip,
                                                timeFormat = uiState.timeFormat,
                                                modifier = Modifier
                                                    .heightIn(min = 80.dp)
                                                    .fillMaxWidth()
                                                    .clickable { onTripSelected(trip) }
                                                    .padding(8.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                        if (uiState.alerts.isNotEmpty()) {
                            item {
                                SectionHeader(stringResource(Res.string.alerts))
                            }
                            items(
                                items = uiState.alerts,
                                key = { alert -> alert.id },
                            ) { alert ->
                                AlertItem(
                                    alert = alert,
                                    modifier = Modifier
                                        .widthIn(max = 400.dp)
                                        .animateItem(),
                                    onClick = {
                                        if ("fr" in Locale.current.language) {
                                            alert.urlFr
                                        } else {
                                            alert.urlEn
                                        }?.let(uriHandler::openUri)
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

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
    )
}