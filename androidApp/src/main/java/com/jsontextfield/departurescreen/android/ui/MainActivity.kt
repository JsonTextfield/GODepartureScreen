package com.jsontextfield.departurescreen.android.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.jsontextfield.departurescreen.android.R
import com.jsontextfield.departurescreen.android.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel by viewModels<MainViewModel>()
        enableEdgeToEdge()
        setContent {
            val trains by mainViewModel.trains.collectAsState()
            val timeRemaining by mainViewModel.timeRemaining.collectAsState()
            LaunchedEffect(Unit) {
                mainViewModel.start(getString(R.string.go_key))
            }
            MyApplicationTheme {
                Scaffold(topBar = {
                    TopAppBar(
                        title = {
                            Text(stringResource(R.string.app_name))
                        },
                        actions = {
                            CountdownTimer(timeRemaining = timeRemaining)
                        },
                    )
                }) { innerPadding ->
                    val orientation = LocalConfiguration.current.orientation
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        TrainListPortrait(
                            trains = trains,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                    else {
                        TrainListLandscape(
                            trains = trains,
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }
            }
        }
    }
}