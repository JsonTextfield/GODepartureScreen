package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.ui.theme.MyApplicationTheme
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.app_name
import departure_screen.composeapp.generated.resources.filter
import departure_screen.composeapp.generated.resources.sort
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(mainViewModel: MainViewModel = koinViewModel<MainViewModel>()) {
    val trains by mainViewModel.displayedTrains.collectAsState()
    val timeRemaining by mainViewModel.timeRemaining.collectAsState()

    var showFilterDialog by remember { mutableStateOf(false) }

    var showSortDialog by remember { mutableStateOf(false) }

    MyApplicationTheme {
        if (showFilterDialog) {
            FilterTrainDialog(
                data = mainViewModel.trains.value,
                selectedItems = mainViewModel.hiddenTrains.value,
                onSelectionChanged = { mainViewModel.setHiddenTrains(it) },
                onDismissRequest = { showFilterDialog = false },
            )
        }
        if (showSortDialog) {
            SortTrainMenu(
                isExpanded = showSortDialog,
                sortMode = mainViewModel.sortMode.value,
                onSortModeChanged = { mainViewModel.setSortMode(it) },
                onDismissRequest = { showSortDialog = false },
            )
        }
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.app_name),
                        modifier = Modifier.semantics { heading() },
                    )
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { showFilterDialog = true },
                        ) {
                            Icon(Icons.Rounded.FilterList, stringResource(Res.string.filter))
                        }
                        IconButton(
                            onClick = { showSortDialog = true },
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Sort, stringResource(Res.string.sort))
                        }
                        CountdownTimer(timeRemaining = timeRemaining)
                    }
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