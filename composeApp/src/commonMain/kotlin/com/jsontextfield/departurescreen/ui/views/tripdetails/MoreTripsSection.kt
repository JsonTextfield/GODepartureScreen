package com.jsontextfield.departurescreen.ui.views.tripdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.ui.Status
import com.jsontextfield.departurescreen.core.ui.TimeFormat
import com.jsontextfield.departurescreen.core.ui.components.TripListHeader
import com.jsontextfield.departurescreen.core.ui.components.TripListItem
import com.jsontextfield.departurescreen.core.ui.components.isEven

@Composable
fun MoreTripsSection(
    moreTrips: List<Trip>,
    status: Status,
    title: String,
    timeFormat: TimeFormat,
    onTripSelected: (Trip) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if ((status == Status.LOADING) || (status == Status.LOADED && moreTrips.isNotEmpty())) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SectionHeader(title)
                if (status == Status.LOADING) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                }
            }
        }
        if (moreTrips.isNotEmpty()) {
            Column(modifier = Modifier.sectionBorder()) {
                TripListHeader(
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp,
                    )
                )
                moreTrips.forEachIndexed { index, trip ->
                    Surface(tonalElevation = if (index.isEven) 1.dp else 0.dp) {
                        TripListItem(
                            trip = trip,
                            timeFormat = timeFormat,
                            modifier = Modifier
                                .heightIn(min = 80.dp)
                                .fillMaxWidth()
                                .clickable { onTripSelected(trip) }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}