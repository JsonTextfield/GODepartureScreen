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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class GoTrainDataSource(private val departureScreenAPI: DepartureScreenAPI) {
    suspend fun getTrains(apiKey: String): List<Train> {
        return try {
            val lines = departureScreenAPI.getNextService(apiKey).nextService.lines
            val trips = departureScreenAPI.getUnionDepartures(apiKey).unionDepartures.trips
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
            val matchingTrip = tripsMap[line.tripNumber]
            if (matchingTrip != null) {
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
                val inFormatter = LocalDateTime.Format {
                    byUnicodePattern("yyyy-MM-dd HH:mm:ss")
                }
                val outFormatter = LocalDateTime.Format {
                    byUnicodePattern("HH:mm")
                }
                val departureTimeString = LocalDateTime
                    .parse(matchingTrip.time, inFormatter)
                    .format(outFormatter)
                Train(
                    code = line.lineCode,
                    name = line.lineName,
                    destination = line.directionName.split(" - ").last(),
                    departureTime = departureTimeString,
                    platform = matchingTrip.platform,
                    color = color,
                    tripOrder = line.tripOrder,
                    info = matchingTrip.info,
                )
            } else {
                null
            }
        }.sortedWith(compareBy({ it.code }, { it.tripOrder }))
    }
}