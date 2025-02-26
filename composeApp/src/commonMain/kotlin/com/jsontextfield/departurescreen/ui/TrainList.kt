package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.getScreenWidth

@Composable
fun TrainList(
    trains: List<Train>,
    modifier: Modifier = Modifier,
) {
    val columns = (getScreenWidth() / 300.dp).toInt()
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier,
    ) {
        itemsIndexed(trains) { index, train ->
            TrainListItem(
                train,
                useAlternateColor = if (columns.isOdd) {
                    index.isEven
                } else {
                    val row = index / columns
                    index.isOdd xor row.isEven
                },
            )
        }
        item {
            Spacer(Modifier.height(100.dp))
        }
    }
}

val Int.isEven: Boolean
    get() = this % 2 == 0
val Int.isOdd: Boolean
    get() = !isEven