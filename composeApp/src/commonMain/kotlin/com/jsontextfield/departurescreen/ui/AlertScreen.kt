package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Alert
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.information_alerts
import departure_screen.composeapp.generated.resources.service_alerts
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(onBackPressed: () -> Unit = {}) {
    val alertViewModel: AlertViewModel = koinViewModel()
    val informationAlerts by alertViewModel.informationAlerts.collectAsState()
    val serviceAlerts by alertViewModel.serviceAlerts.collectAsState()
    LaunchedEffect(Unit) {
        alertViewModel.loadAlerts()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Alerts",
                        modifier = Modifier.semantics { heading() }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                modifier = Modifier.shadow(4.dp)
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    start = WindowInsets.safeDrawing.asPaddingValues().calculateLeftPadding(LayoutDirection.Ltr),
                    end = WindowInsets.safeDrawing.asPaddingValues().calculateRightPadding(LayoutDirection.Ltr),
                ).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding())
        ) {
            if (serviceAlerts.isNotEmpty()) {
                item {
                    Text(stringResource(Res.string.service_alerts), style = MaterialTheme.typography.headlineMedium)
                }
                items(serviceAlerts, key = { it.id }) { alert ->
                    AlertItem(
                        alert = alert,
                    )
                }
            }
            if (informationAlerts.isNotEmpty()) {
                item {
                    Text(stringResource(Res.string.information_alerts), style = MaterialTheme.typography.headlineMedium)
                }
                items(informationAlerts, key = { it.id }) { alert ->
                    AlertItem(
                        alert = alert,
                    )
                }
            }
        }
    }
}

@Composable
fun AlertItem(
    alert: Alert,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = alert.subject,
            modifier = modifier.semantics { heading() },
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = alert.body,
            style = MaterialTheme.typography.bodySmall
        )
    }
}