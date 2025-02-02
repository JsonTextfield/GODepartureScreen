package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.getScreenWidth
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(getScreenWidth() / 3),
    ) {
        itemsIndexed(trains) { index, train ->
            TrainListItem(train, index % 2 == 0)
        }
    }
}

private val sampleTrains = listOf(
    Train(
        code = "LW",
        departureTime = "10:00",
        destination = "Station A",
        info = "Wait / Attendez"
    ),
    Train(
        code = "LE",
        departureTime = "11:00",
        destination = "Station B",
        info = "Wait / Attendez"
    ),
    Train(
        code = "BR",
        departureTime = "12:00",
        destination = "Station C",
        info = "Wait / Attendez"
    ),
    Train(
        code = "RH",
        departureTime = "13:00",
        destination = "Station D",
        info = "Wait / Attendez"
    ),
)

@Preview
//@Preview(
//    device = "spec:width=360dp,height=640dp,orientation=landscape"
//)
@Composable
fun TrainListPortraitPreview() {// Sample data for the previews
    TrainListPortrait(trains = sampleTrains, modifier = Modifier.fillMaxSize())
}