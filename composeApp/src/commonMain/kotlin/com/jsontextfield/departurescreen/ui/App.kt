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
import com.jsontextfield.departurescreen.ui.theme.MyApplicationTheme
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    title: String = "",
    isPortrait: Boolean = true,
) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val trains by mainViewModel.trains.collectAsState()
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()
    LaunchedEffect(Unit) {
        mainViewModel.start("API_KEY")
    }
    MyApplicationTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(title)
                },
                actions = {
                    CountdownTimer(timeRemaining = timeRemaining)
                },
            )
        }) { innerPadding ->
            if (isPortrait) {
                TrainListPortrait(
                    trains = trains,
                    modifier = Modifier.padding(innerPadding),
                )
            } else {
                TrainListLandscape(
                    trains = trains,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}