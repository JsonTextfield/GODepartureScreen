package com.jsontextfield.departurescreen.ui

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.entities.CombinedStation
import com.jsontextfield.departurescreen.entities.Station
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.favourite
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationComboBox(
    items: List<CombinedStation> = emptyList(),
    selectedItem: CombinedStation? = null,
    onItemSelected: (CombinedStation) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.padding(
            start = WindowInsets.safeDrawing.asPaddingValues()
                .calculateStartPadding(LayoutDirection.Ltr),
        )
    ) {
        ListItem(
            headlineContent = {
                Text(
                    selectedItem?.name.orEmpty(),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee()
                )
            },
            trailingContent = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .semantics {
                    collectionInfo = CollectionInfo(
                        rowCount = items.size,
                        columnCount = 1,
                    )
                }
        ) {
            items.forEachIndexed { index, station ->
                DropdownMenuItem(
                    contentPadding = PaddingValues(0.dp),
                    enabled = station.isEnabled,
                    text = {
                        ListItem(
                            modifier = Modifier.alpha(if (station.isEnabled) 1f else 0.5f),
                            colors = ListItemDefaults.colors(
                                containerColor = if ((station.codes intersect selectedItem?.codes.orEmpty()).isNotEmpty()) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else if (!station.isEnabled) {
                                    MaterialTheme.colorScheme.surfaceContainerLow
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            headlineContent = {
                                Text(station.name)
                            },
                            trailingContent = {
                                if (station.isFavourite) {
                                    Icon(
                                        Icons.Rounded.Star,
                                        contentDescription = stringResource(Res.string.favourite)
                                    )
                                }
                            }
                        )
                    },
                    onClick = {
                        onItemSelected(station)
                        expanded = false
                    },
                    modifier = Modifier.semantics {
                        collectionItemInfo = CollectionItemInfo(
                            rowIndex = index,
                            rowSpan = 1,
                            columnIndex = 0,
                            columnSpan = 1,
                        )
                    }
                )
            }
        }
    }
}