package com.jsontextfield.departurescreen.data

import androidx.compose.ui.graphics.Color
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.network.model.UnionDeparturesResponse
import com.jsontextfield.departurescreen.ui.theme.barrie
import com.jsontextfield.departurescreen.ui.theme.kitchener
import com.jsontextfield.departurescreen.ui.theme.lakeshoreEast
import com.jsontextfield.departurescreen.ui.theme.lakeshoreWest
import com.jsontextfield.departurescreen.ui.theme.milton
import com.jsontextfield.departurescreen.ui.theme.richmondHill
import com.jsontextfield.departurescreen.ui.theme.stouffville
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class GoTrainDataSource(private val departureScreenAPI: DepartureScreenAPI) : IGoTrainDataSource {
    override suspend fun getTrains(): List<Train> {
        return try {
            val lines = departureScreenAPI.getNextService().nextService.lines
            val trips = departureScreenAPI.getUnionDepartures().unionDepartures.trips
            mergeLinesAndTrips(lines, trips)
        } catch (exception: Exception) {
            exception.printStackTrace()
            emptyList()
        }
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    private fun mergeLinesAndTrips(
        lines: List<NextServiceResponse.NextService.Line>,
        trips: List<UnionDeparturesResponse.UnionDepartures.Trip>
    ): List<Train> {
        val tripsMap = trips.associateBy { it.tripNumber }

        return lines.mapNotNull { line ->
            tripsMap[line.tripNumber]?.let { matchingTrip ->
                val color: Color = when (line.lineName) {
                    "Stouffville" -> stouffville
                    "Richmond Hill" -> richmondHill
                    "Milton" -> milton
                    "Lakeshore West" -> lakeshoreWest
                    "Lakeshore East" -> lakeshoreEast
                    "Barrie" -> barrie
                    "Kitchener" -> kitchener
                    else -> Color.Gray
                }
                val inFormatter = DateTimeComponents.Format {
                    byUnicodePattern("yyyy-MM-dd HH:mm:ss")
                }
                val outFormatter = DateTimeComponents.Format {
                    byUnicodePattern("HH:mm")
                }
                val departureTime = Instant
                    .parse(matchingTrip.time, inFormatter)
                val departureTimeString = departureTime
                    .format(outFormatter)
                Train(
                    id = line.tripNumber,
                    code = line.lineCode,
                    name = line.lineName,
                    destination = line.directionName.split(" - ").last(),
                    departureTime = departureTime,
                    departureTimeString = departureTimeString,
                    platform = matchingTrip.platform,
                    color = color,
                    tripOrder = line.tripOrder,
                    info = matchingTrip.info,
                )
            }
        }.sortedBy { it.departureTime }
    }
}