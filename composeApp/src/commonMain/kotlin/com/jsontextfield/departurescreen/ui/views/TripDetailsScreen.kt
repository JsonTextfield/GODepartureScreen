@file:OptIn(ExperimentalMaterial3Api::class)

package com.jsontextfield.departurescreen.ui.views

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.SquircleShape
import com.jsontextfield.departurescreen.core.ui.components.AlertItem
import com.jsontextfield.departurescreen.core.ui.components.BackButton
import com.jsontextfield.departurescreen.core.ui.components.TripCodeBox
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import com.jsontextfield.departurescreen.core.ui.viewmodels.TripDetailsViewModel
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.alerts
import departure_screen.composeapp.generated.resources.stops
import org.jetbrains.compose.resources.stringResource

@Composable
fun TripDetailsScreen(
    tripDetailsViewModel: TripDetailsViewModel,
    onBackPressed: () -> Unit
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
                                .background(color = lineColours[uiState.lineCode] ?: Color.Gray, shape = SquircleShape)
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
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                start = WindowInsets.safeDrawing.asPaddingValues().calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
                end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
                bottom = 100.dp,
            )
        ) {
            if (uiState.stops.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.animateItem()) {
                        Text(
                            text = stringResource(Res.string.stops),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Column(modifier = Modifier.padding(8.dp)) {
                            uiState.stops.forEachIndexed { index, stop ->
                                Text(
                                    text = stop,
                                    style = if (stop == uiState.selectedStop) {
                                        MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                        )
                                    } else {
                                        MaterialTheme.typography.bodyMedium
                                    },
                                    modifier = Modifier
                                        .alpha(if (index < uiState.stops.indexOf(uiState.selectedStop)) 0.5f else 1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            if (uiState.alerts.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.alerts),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
                itemsIndexed(items = uiState.alerts, key = { index, alert -> alert.id }) { index, alert ->
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
                        })
                }
            }
        }
    }
}