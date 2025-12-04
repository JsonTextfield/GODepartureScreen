package com.jsontextfield.departurescreen.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Station
import departure_screen.core.generated.resources.Res
import departure_screen.core.generated.resources.favourite
import departure_screen.core.generated.resources.round_star_24
import departure_screen.core.generated.resources.round_star_border_24
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun StationListItem(
    station: Station,
    modifier: Modifier = Modifier,
    onFavouriteClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = station.name,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                station.types.forEach {
                    Text(
                        text = stringResource(it.stringResId),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ).padding(horizontal = 4.dp)
                    )
                }
            }
        }

        IconButton(onFavouriteClick) {
            Icon(
                if (station.isFavourite) {
                    painterResource(Res.drawable.round_star_24)
                } else {
                    painterResource(Res.drawable.round_star_border_24)
                },
                contentDescription = stringResource(Res.string.favourite)
            )
        }
    }
}