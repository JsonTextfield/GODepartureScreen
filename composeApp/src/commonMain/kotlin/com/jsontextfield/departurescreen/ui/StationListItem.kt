package com.jsontextfield.departurescreen.ui

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.jsontextfield.departurescreen.entities.Station

@Composable
fun StationListItem(
    station: Station,
    isSelected: Boolean = false,
) {
    ListItem(
        modifier = Modifier.alpha(if (station.isEnabled) 1f else 0.5f),
        colors = ListItemDefaults.colors(
            containerColor = if (isSelected) {
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
        supportingContent = {
        },
    )
}