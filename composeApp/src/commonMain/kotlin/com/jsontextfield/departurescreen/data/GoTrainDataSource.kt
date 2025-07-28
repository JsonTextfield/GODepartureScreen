package com.jsontextfield.departurescreen.data

import androidx.compose.ui.graphics.Color
import com.jsontextfield.departurescreen.Alert
import com.jsontextfield.departurescreen.Train
import com.jsontextfield.departurescreen.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.network.model.Alerts
import com.jsontextfield.departurescreen.ui.theme.trainColours
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Instant
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class GoTrainDataSource(
    private val departureScreenAPI: DepartureScreenAPI
) : IGoTrainDataSource {
    override suspend fun getTrains(): List<Train> = coroutineScope {
        try {
            val nextService = async { departureScreenAPI.getNextService() }.await()
            val unionDepartures = async { departureScreenAPI.getUnionDepartures() }.await()

            val lastUpdated = nextService.metadata?.timestamp.orEmpty()
            val lines = nextService.nextService?.lines
            val trips = unionDepartures.unionDepartures?.trips
            val tripsMap = trips?.associateBy { it.tripNumber } ?: emptyMap()

            lines?.mapNotNull { line ->
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
                        lastUpdated = Instant.parse(lastUpdated, inFormatter),
                    )
                }
            }?.sortedBy { it.departureTime } ?: emptyList()
        } catch (exception: IOException) {
            throw exception
        } catch (exception: Exception) {
            exception.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getServiceAlerts(): List<Alert> = processAlerts(departureScreenAPI.getServiceAlerts())

    override suspend fun getInformationAlerts(): List<Alert> = processAlerts(departureScreenAPI.getInfromationAlerts())

    private fun processAlerts(alerts: Alerts): List<Alert> {
        return try {
            alerts.messages?.message?.filter { message ->
                message.stops.any { it.code == "UN" } && message.lines.all { it.code != "UP" }
            }?.map { message ->
                Alert(
                    id = message.code,
                    date = Instant.parse(message.postedDateTime, inFormatter),
                    subjectEn = message.subjectEnglish.orEmpty().trim(),
                    subjectFr = message.subjectFrench.orEmpty().trim(),
                    bodyEn = message.bodyEnglish.orEmpty().trim(),
                    bodyFr = message.bodyFrench.orEmpty().trim(),
                )
            }?.sortedByDescending { it.date } ?: emptyList()
        } catch (exception: IOException) {
            throw exception
        } catch (exception: Exception) {
            exception.printStackTrace()
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