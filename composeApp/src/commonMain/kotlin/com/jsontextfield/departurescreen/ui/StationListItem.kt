package com.jsontextfield.departurescreen.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import com.jsontextfield.departurescreen.entities.CombinedStation
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.favourite
import org.jetbrains.compose.resources.stringResource

@Composable
fun StationListItem(
    station: CombinedStation,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier.alpha(if (station.isEnabled) 1f else 0.5f),
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
            Text(
                station.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
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
}