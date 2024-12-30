package com.jsontextfield.departurescreen.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.android.R
import com.jsontextfield.departurescreen.android.ui.theme.MyApplicationTheme
import com.jsontextfield.departurescreen.data.GoTrainDataSource
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var trains by remember { mutableStateOf(emptyList<Train>()) }
            var timeRemaining by remember { mutableIntStateOf(0) }
            LaunchedEffect(timeRemaining) {
                if (timeRemaining <= 0) {
                    trains = GoTrainDataSource().getTrains(getString(R.string.go_key))
                    timeRemaining = 30_000
                }
                else {
                    delay(1000)
                    timeRemaining -= 1000
                }
            }
            MyApplicationTheme {
                Scaffold(topBar = {
                    TopAppBar(
                        title = {
                            Text(stringResource(R.string.app_name))
                        },
                        actions = {
                            CountdownTimer(timeRemaining = timeRemaining,)
                        },
                    )
                }) { innerPadding ->
                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
                        itemsIndexed(trains) { index, train ->
                            TrainListItem(train, index % 2 == 0)
                        }
                    }
                }
            }
        }
    }
}