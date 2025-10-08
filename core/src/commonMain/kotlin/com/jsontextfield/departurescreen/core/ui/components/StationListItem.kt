package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.jsontextfield.departurescreen.core.entities.CombinedStation
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.favourite
import org.jetbrains.compose.resources.stringResource

@Composable
fun StationListItem(
    station: CombinedStation,
    modifier: Modifier = Modifier,
    onFavouriteClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
    ) {
        Text(
            text = station.name,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        IconButton(onFavouriteClick) {
            Icon(
                if (station.isFavourite) {
                    Icons.Rounded.Star
                } else {
                    Icons.Rounded.StarBorder
                },
                contentDescription = stringResource(Res.string.favourite)
            )
        }
    }
}