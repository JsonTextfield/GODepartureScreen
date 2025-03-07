package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Surface
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
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 100.dp,
        )
    ) {
        itemsIndexed(trains) { index, train ->
            val useAlternateColor = if (columns.isOdd) {
                index.isEven
            }
            else {
                val row = index / columns
                index.isOdd xor row.isEven
            }
            Surface(
                tonalElevation = if (useAlternateColor) 1.dp else 0.dp,
            ) {
                TrainListItem(
                    train,
                    modifier = Modifier.fillMaxWidth().padding(12.dp)
                )
            }
        }
    }
}

val Int.isEven: Boolean
    get() = this % 2 == 0
val Int.isOdd: Boolean
    get() = !isEven