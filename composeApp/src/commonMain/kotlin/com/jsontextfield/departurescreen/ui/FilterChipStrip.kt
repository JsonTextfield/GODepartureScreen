package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.entities.Train
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.all
import departure_screen.composeapp.generated.resources.filter
import org.jetbrains.compose.resources.stringResource

@Composable
fun FilterChipStrip(
    data: List<Train>,
    selectedItems: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filter = stringResource(Res.string.filter)
    LazyRow(
        modifier = modifier.semantics {
            contentDescription = filter
            collectionInfo = CollectionInfo(
                rowCount = 1,
                columnCount = data.size + 1,
            )
        },
        contentPadding = PaddingValues(
            start = WindowInsets.safeDrawing.asPaddingValues().calculateLeftPadding(LayoutDirection.Ltr) + 16.dp,
            end = WindowInsets.safeDrawing.asPaddingValues().calculateRightPadding(LayoutDirection.Ltr) + 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FilterChip(
                selected = selectedItems.isEmpty() || data.isEmpty(),
                onClick = {
                    onSelectionChanged(emptySet())
                },
                label = {
                    Text(
                        stringResource(Res.string.all),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(8.dp)
                    )
                },
                modifier = Modifier
                    .animateItem()
                    .semantics {
                        collectionItemInfo = CollectionItemInfo(
                            rowIndex = 0,
                            columnIndex = 0,
                            rowSpan = 1,
                            columnSpan = 1,
                        )
                    },
                shape = RoundedCornerShape(4.dp),
            )
        }
        itemsIndexed(data, key = { _, train -> train.code }) { index, train ->
            FilterChip(
                selected = train.code in selectedItems,
                onClick = {
                    val newSelection = if (train.code in selectedItems) {
                        selectedItems - train.code
                    } else {
                        selectedItems + train.code
                    }
                    onSelectionChanged(newSelection)
                },
                label = {
                    Text(
                        train.code,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (train.code in selectedItems) Color.White else Color.Unspecified
                        ),
                        modifier = Modifier.padding(8.dp).semantics {
                            contentDescription = train.name
                        }
                    )
                },
                modifier = Modifier
                    .animateItem()
                    .semantics {
                        collectionItemInfo = CollectionItemInfo(
                            rowIndex = 0,
                            columnIndex = index + 1,
                            rowSpan = 1,
                            columnSpan = 1,
                        )
                    },
                shape = RoundedCornerShape(4.dp),
                colors = FilterChipDefaults.filterChipColors().copy(
                    selectedContainerColor = train.color,
                )
            )
        }
    }
}