package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.getScreenWidth
import kotlin.math.ceil

@Composable
fun TrainList(
    trains: List<Train>,
    modifier: Modifier = Modifier,
) {
    val columns = (getScreenWidth() / 300).coerceIn(1, 4)
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.semantics {
            collectionInfo = CollectionInfo(
                rowCount = ceil(trains.size.toDouble() / columns).toInt(),
                columnCount = columns,
            )
        },
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 100.dp,
        )
    ) {
        itemsIndexed(trains, key = { _, train -> train.id }) { index, train ->
            val useAlternateColor = if (columns.isOdd) {
                index.isEven
            }
            else {
                val row = index / columns
                index.isOdd xor row.isEven
            }
            Surface(
                tonalElevation = if (useAlternateColor) 1.dp else 0.dp,
                modifier = Modifier.animateItem().semantics {
                    collectionItemInfo = CollectionItemInfo(
                        rowIndex = ceil(index.toDouble() / columns).toInt(),
                        columnIndex = index % columns,
                        rowSpan = 1,
                        columnSpan = 1,
                    )
                }
            ) {
                TrainListItem(
                    train,
                    modifier = Modifier
                        .heightIn(min = 80.dp)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(
                            start = WindowInsets.safeDrawing.asPaddingValues().calculateStartPadding(
                                LayoutDirection.Ltr),
                            end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(
                                LayoutDirection.Ltr),
                        )
                )
            }
        }
    }
}

val Int.isEven: Boolean
    get() = this % 2 == 0
val Int.isOdd: Boolean
    get() = !isEven