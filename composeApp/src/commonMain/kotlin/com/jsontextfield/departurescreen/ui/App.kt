package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.jsontextfield.departurescreen.ui.theme.MyApplicationTheme
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val mainViewModel = koinViewModel<MainViewModel>()
    val trains by mainViewModel.trains.collectAsState()
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                mainViewModel.refreshData()
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
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
                trains = trains, Modifier.padding(
                    top = innerPadding.calculateTopPadding(),
                )
            )
        }
    }
}