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
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.getScreenWidth
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TrainList(
    trains: List<Train>,
    modifier: Modifier = Modifier,
    isPortrait: Boolean = true,
) {
    if (isPortrait) {
        LazyColumn(modifier = modifier) {
            itemsIndexed(trains) { index, train ->
                TrainListItem2(train, index % 2 == 0)
            }
        }
    } else {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Adaptive(max(300.dp, getScreenWidth() / 3)),
        ) {
            itemsIndexed(trains) { index, train ->
                TrainListItem2(train, index % 2 == 0)
            }
        }
    }
}

private val sampleTrains = listOf(
    Train(
        code = "LW",
        departureTimeString = "10:00",
        destination = "Station A",
        info = "Wait / Attendez"
    ),
    Train(
        code = "LE",
        departureTimeString = "11:00",
        destination = "Station B",
        info = "Wait / Attendez"
    ),
    Train(
        code = "BR",
        departureTimeString = "12:00",
        destination = "Station C",
        info = "Wait / Attendez"
    ),
    Train(
        code = "RH",
        departureTimeString = "13:00",
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
    TrainList(trains = sampleTrains, modifier = Modifier.fillMaxSize())
}