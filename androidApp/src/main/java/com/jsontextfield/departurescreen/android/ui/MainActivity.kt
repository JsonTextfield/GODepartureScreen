package com.jsontextfield.departurescreen.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.android.ui.theme.MyApplicationTheme
import com.jsontextfield.departurescreen.data.GoTrainDataSource
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var trains by remember { mutableStateOf(emptyList<Train>()) }
            var remainingMs by remember { mutableIntStateOf(30000) }
            LaunchedEffect(Unit) {
                trains = GoTrainDataSource().getTrains()
                while (true) {
                    delay(20)
                    remainingMs -= 20
                    if (remainingMs <= 0) {
                        trains = GoTrainDataSource().getTrains()
                        remainingMs = 30000
                    }
                }
            }
            MyApplicationTheme {

                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = {
                        Text("Departure Screen")
                    }, actions = {
                        Timer(
                            totalMs = 30_000,
                            remainingMs = remainingMs,
                        )
                    })
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

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
