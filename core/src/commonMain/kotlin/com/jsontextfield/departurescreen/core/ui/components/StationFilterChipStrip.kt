package com.jsontextfield.departurescreen.core.ui.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.ui.StationType
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.all
import departure_screen.core.generated.resources.filter
import org.jetbrains.compose.resources.stringResource

@Composable
fun StationFilterChipStrip(
    stationType: StationType?,
    onSelectionChanged: (StationType?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filter = stringResource(Res.string.filter)
    LazyRow(
        modifier = modifier.semantics {
            contentDescription = filter
            collectionInfo = CollectionInfo(
                rowCount = 1,
                columnCount = 3,
            )
        },
        contentPadding = PaddingValues(
            start = WindowInsets.safeDrawing.asPaddingValues().calculateLeftPadding(LayoutDirection.Ltr) + 16.dp,
            end = WindowInsets.safeDrawing.asPaddingValues().calculateRightPadding(LayoutDirection.Ltr) + 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = stationType == null,
                onClick = {
                    onSelectionChanged(null)
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

        itemsIndexed(StationType.entries, key = { _, type -> type.name }) { index, type ->
            FilterChip(
                selected = type == stationType,
                onClick = {
                    onSelectionChanged(type)
                },
                label = {
                    Text(
                        text = stringResource(type.stringResId),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
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
            )
        }
    }
}