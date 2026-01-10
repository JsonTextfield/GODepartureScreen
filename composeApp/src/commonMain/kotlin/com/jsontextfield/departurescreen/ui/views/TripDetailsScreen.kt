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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import com.jsontextfield.departurescreen.core.ui.components.AlertItem
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.components.TripCodeBox
import com.jsontextfield.departurescreen.core.ui.viewmodels.TripDetailsViewModel
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.alerts
import departure_screen.composeapp.generated.resources.more_trips
import departure_screen.composeapp.generated.resources.stops
import org.jetbrains.compose.resources.stringResource

@Composable
fun TripDetailsScreen(
    tripDetailsViewModel: TripDetailsViewModel,
    onBackPressed: () -> Unit
) {
    val uiState by tripDetailsViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    uiState.trip?.let { trip ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TripCodeBox(
                                tripCode = trip.code,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color = trip.color, shape = SquircleShape)
                            )
                            Text(text = trip.destination)
                        }
                    }
                },
                navigationIcon = {
                    BackButton(onBackPressed = onBackPressed)
                },
                modifier = Modifier.shadow(4.dp)
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(it), contentPadding = PaddingValues(
                start = WindowInsets.safeDrawing.asPaddingValues().calculateStartPadding(LayoutDirection.Ltr),
                end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(LayoutDirection.Ltr),
            )
        ) {
            item {
                Text(
                    text = stringResource(Res.string.stops),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(12.dp),
                )
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    uiState.stops.forEach { stop ->
                        Text(
                            text = stop,
                            style = if (stop == uiState.currentStop?.name) {
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                MaterialTheme.typography.bodyMedium
                            },
                        )
                    }
                }
            }
            item {
                Text(
                    text = stringResource(Res.string.more_trips),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(12.dp),
                )
            }
            if (uiState.alerts.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.alerts),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(12.dp),
                    )
                }
                items(uiState.alerts) { alert ->
                    AlertItem(alert = alert, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}