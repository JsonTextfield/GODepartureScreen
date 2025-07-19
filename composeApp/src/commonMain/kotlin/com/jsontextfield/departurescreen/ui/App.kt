package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.ui.menu.Action
import com.jsontextfield.departurescreen.ui.menu.ActionBar
import com.jsontextfield.departurescreen.ui.menu.getActions
import com.jsontextfield.departurescreen.ui.theme.MyApplicationTheme
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(mainViewModel: MainViewModel = koinViewModel()) {
    val allTrains by mainViewModel.allTrains.collectAsState()
    val hiddenTrains by mainViewModel.visibleTrains.collectAsState()
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()
    App(
        allTrains = allTrains,
        visibleTrains = hiddenTrains,
        timeRemaining = timeRemaining,
        actions = getActions(mainViewModel),
        onSetVisibleTrains = mainViewModel::setVisibleTrains,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    allTrains: List<Train>,
    visibleTrains: Set<String>,
    timeRemaining: Int,
    actions: List<Action>,
    onSetVisibleTrains: (Set<String>) -> Unit,
) {
    MyApplicationTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.app_name),
                        modifier = Modifier
                            .semantics { heading() }
                            .basicMarquee(),
                    )
                },
                actions = {
                    ActionBar(actions)
                    Spacer(Modifier.width(8.dp))
                    CountdownTimer(timeRemaining)
                },
                modifier = Modifier.shadow(4.dp)
            )
        }) { innerPadding ->
            Column(modifier = Modifier.padding(top = innerPadding.calculateTopPadding())) {
                FilterChipStrip(
                    data = allTrains.distinctBy { it.code to it.name }.sortedBy { it.code },
                    selectedItems = visibleTrains,
                    onSelectionChanged = onSetVisibleTrains
                )
                Surface {
                    TrainList(trains = allTrains.filter { it.isVisible })
                }
            }
        }
    }
}