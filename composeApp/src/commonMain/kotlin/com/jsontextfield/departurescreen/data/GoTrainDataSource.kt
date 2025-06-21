package com.jsontextfield.departurescreen.data

import androidx.compose.ui.graphics.Color
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.ui.theme.trainColours
import kotlinx.datetime.Instant
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class GoTrainDataSource(private val departureScreenAPI: DepartureScreenAPI) : IGoTrainDataSource {
    override suspend fun getTrains(): List<Train> {
        return try {
            val lines = departureScreenAPI.getNextService().nextService.lines
            val trips = departureScreenAPI.getUnionDepartures().unionDepartures.trips
            val tripsMap = trips.associateBy { it.tripNumber }

            return lines.mapNotNull { line ->
                tripsMap[line.tripNumber]?.let { matchingTrip ->
                    Train(
                        id = line.tripNumber,
                        code = line.lineCode,
                        name = line.lineName,
                        destination = line.directionName.split(" - ").last(),
                        departureTime = Instant.parse(matchingTrip.time, inFormatter),
                        platform = matchingTrip.platform,
                        color = trainColours[line.lineName] ?: Color.Gray,
                        tripOrder = line.tripOrder,
                        info = matchingTrip.info,
                    )
                }
            }.sortedBy { it.departureTime }
        } catch (exception: IOException) {
            throw exception
        } catch (exception: Exception) {
            emptyList()
        }
    }
    companion object {
        @OptIn(FormatStringsInDatetimeFormats::class)
        val inFormatter = DateTimeComponents.Format {
            byUnicodePattern("yyyy-MM-dd HH:mm:ss")
        }
    }
}