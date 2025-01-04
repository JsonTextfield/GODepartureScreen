package com.jsontextfield.departurescreen.android.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train

@Composable
fun TrainListPortrait(
    trains: List<Train>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(trains) { index, train ->
            TrainListItem(train, index % 2 == 0)
        }
    }
}

@Composable
fun TrainListLandscape(
    trains: List<Train>,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(configuration.screenWidthDp.dp / 3),
    ) {
        itemsIndexed(trains) { index, train ->
            TrainListItem(train, index % 2 == 0)
        }
    }
}