package com.jsontextfield.departurescreen.ui.views.tripdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Schedule
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import com.jsontextfield.departurescreen.core.ui.components.TripDetailStopListHeader
import com.jsontextfield.departurescreen.core.ui.components.TripDetailStopListItem
import com.jsontextfield.departurescreen.core.ui.components.isEven
import departure_screen.composeapp.generated.resources.Res
import departure_screen.composeapp.generated.resources.stops
import org.jetbrains.compose.resources.stringResource

@Composable
fun StopsSection(
    stops: List<Schedule>,
    timeFormat: TimeFormat,
    selectedStop: String,
    onStopSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SectionHeader(stringResource(Res.string.stops))
        Column(modifier = Modifier.sectionBorder()) {
            TripDetailStopListHeader(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
            )
            stops.forEachIndexed { index, stop ->
                Surface(tonalElevation = if (index.isEven) 1.dp else 0.dp) {
                    TripDetailStopListItem(
                        stop = stop,
                        timeFormat = timeFormat,
                        isSelected = stop.name == selectedStop,
                        isEnabled = index >= stops.indexOfFirst { it.name == selectedStop },
                        modifier = Modifier
                            .heightIn(min = 60.dp)
                            .clickable(onClick = { onStopSelected(stop.name) })
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}