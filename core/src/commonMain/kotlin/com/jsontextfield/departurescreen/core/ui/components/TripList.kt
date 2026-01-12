package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Trip
import kotlin.math.ceil

@Composable
fun TripList(
    trips: List<Trip>,
    onItemClick: (Trip) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val widthDp =
        (LocalWindowInfo.current.containerSize.width / density.density - WindowInsets.safeDrawing.asPaddingValues()
            .calculateLeftPadding(
                LayoutDirection.Ltr
            ).value - WindowInsets.safeDrawing.asPaddingValues()
            .calculateRightPadding(LayoutDirection.Ltr).value).toInt()
    val columns = (widthDp / 360).coerceIn(1, 4)
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.semantics {
            collectionInfo = CollectionInfo(
                rowCount = ceil(trips.size.toDouble() / columns).toInt(),
                columnCount = columns,
            )
        },
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding() + 100.dp,
        )
    ) {
        items(columns) {
            TripListHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(
                        start = WindowInsets.safeDrawing.asPaddingValues()
                            .calculateStartPadding(LayoutDirection.Ltr),
                        end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(LayoutDirection.Ltr),
                    )
            )
        }
        itemsIndexed(trips, key = { _, train -> train.id }) { index, trip ->
            val useAlternateColor = if (columns.isOdd) {
                index.isEven
            } else {
                val row = index / columns
                index.isOdd xor row.isEven
            }
            Surface(
                tonalElevation = if (useAlternateColor) 1.dp else 0.dp,
                modifier = Modifier.semantics {
                    collectionItemInfo = CollectionItemInfo(
                        rowIndex = index / columns,
                        columnIndex = index % columns,
                        rowSpan = 1,
                        columnSpan = 1,
                    )
                }.animateItem()
            ) {
                TripListItem(
                    trip,
                    modifier = Modifier
                        .heightIn(min = 80.dp)
                        .fillMaxWidth()
                        .clickable(
                            enabled = !trip.isCancelled,
                            onClick = { onItemClick(trip) },
                        )
                        .padding(8.dp)
                        .padding(
                            start = WindowInsets.safeDrawing.asPaddingValues()
                                .calculateStartPadding(LayoutDirection.Ltr),
                            end = WindowInsets.safeDrawing.asPaddingValues().calculateEndPadding(LayoutDirection.Ltr),
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