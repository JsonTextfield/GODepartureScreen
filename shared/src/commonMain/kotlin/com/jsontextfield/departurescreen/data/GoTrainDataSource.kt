package com.jsontextfield.departurescreen.data

import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.network.model.UnionDeparturesResponse
import com.jsontextfield.departurescreen.ui.theme.Barrie
import com.jsontextfield.departurescreen.ui.theme.Kitchener
import com.jsontextfield.departurescreen.ui.theme.LakeshoreEast
import com.jsontextfield.departurescreen.ui.theme.LakeshoreWest
import com.jsontextfield.departurescreen.ui.theme.Milton
import com.jsontextfield.departurescreen.ui.theme.RichmondHill
import com.jsontextfield.departurescreen.ui.theme.Stouffville
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class GoTrainDataSource {
    suspend fun getTrains(): List<Train> {
        val apiKey = ""
        return try {
            val lines = DepartureScreenAPI.getNextService(apiKey).nextService.lines
            val trips = DepartureScreenAPI.getUnionDepartures(apiKey).unionDepartures.trips
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
                val color: Int = when (line.lineName) {
                    "Stouffville" -> Stouffville
                    "Richmond Hill" -> RichmondHill
                    "Milton" -> Milton
                    "Lakeshore West" -> LakeshoreWest
                    "Lakeshore East" -> LakeshoreEast
                    "Barrie" -> Barrie
                    "Kitchener" -> Kitchener
                    else -> 0xFF000000.toInt()
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
                )
            } else {
                null
            }
        }.sortedWith(compareBy({ it.code }, { it.tripOrder }))
    }
}