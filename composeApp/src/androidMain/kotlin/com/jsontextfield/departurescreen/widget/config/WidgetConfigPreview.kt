@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.widget.config

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.R
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.SortMode
import com.jsontextfield.departurescreen.core.ui.components.TripListItem
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@Composable
fun WidgetConfigPreview(
    widgetConfig: WidgetConfig = WidgetConfig(),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = widgetConfig.opacity),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(widgetConfig.selectedStop?.name.orEmpty())
            LazyColumn(
                userScrollEnabled = true,
                modifier = Modifier.weight(1f),
            ) {
                items(
                    trips.sortedWith(
                        if (widgetConfig.sortMode == SortMode.TIME) {
                            compareBy { it.departureTime }
                        } else {
                            compareBy({ it.code }, { it.destination })
                        }
                    )
                ) { trip ->
                    TripListItem(
                        trip = trip,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(painterResource(R.drawable.refresh_24px), null)
                Text(stringResource(R.string.updated, "12:34:56"))
            }
        }
    }
}

private val trips = List(4) {
    val line = lineColours.keys.random()
    Trip(
        id = "$it",
        code = line,
        destination = "Destination",
        platform = "${2 * it + 1}",
        color = lineColours[line] ?: Color.Gray,
        departureTime = Clock.System.now() + it.minutes,
        lastUpdated = Clock.System.now() - 10.minutes,
    )
}