package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.getScreenWidth
import com.jsontextfield.departurescreen.ui.theme.MyApplicationTheme
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.api_key
import departure_screen.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val mainViewModel = koinViewModel<MainViewModel>()
    val trains by mainViewModel.trains.collectAsState()
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()
    val apiKey = stringResource(Res.string.api_key)
    LaunchedEffect(Unit) {
        mainViewModel.start(apiKey)
    }
    MyApplicationTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.app_name),
                        modifier = Modifier.semantics { heading() },
                    )
                },
                actions = {
                    CountdownTimer(timeRemaining = timeRemaining)
                },
            )
        }) { innerPadding ->
            TrainList(
                trains = trains,
                modifier = Modifier.padding(innerPadding),
                isPortrait = getScreenWidth() < 600.dp,
            )
        }
    }
}