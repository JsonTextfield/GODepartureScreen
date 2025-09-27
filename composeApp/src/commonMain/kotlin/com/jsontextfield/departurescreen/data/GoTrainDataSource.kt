package com.jsontextfield.departurescreen.data

import androidx.compose.ui.graphics.Color
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.network.model.Alerts
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import kotlinx.datetime.Instant
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class GoTrainDataSource(
    private val departureScreenAPI: DepartureScreenAPI
) : IGoTrainDataSource {
    override suspend fun getTrains(stationCode: String): List<Trip> {
        return try {
            val nextService = departureScreenAPI.getNextService(stationCode)
            val exceptions = departureScreenAPI.getExceptions()

            val lastUpdated = nextService.metadata?.timestamp.orEmpty()
            val lines = nextService.nextService?.lines
            val cancelledTrips = exceptions.trip.filter { it.isCancelled == "1" }.map { it.tripNumber }

            val result = lines?.map { line ->
                val departureTime =
                    Instant.parseOrNull(line.computedDepartureTime.orEmpty(), inFormatter)
                        ?: Instant.parseOrNull(line.scheduledDepartureTime.orEmpty(), inFormatter)
                        ?: Instant.fromEpochMilliseconds(0)
                val platform =
                    line.actualPlatform.takeIf { !it.isNullOrBlank() }
                        ?: line.scheduledPlatform.takeIf { !it.isNullOrBlank() }
                        ?: "-"

                val lineCode = if (line.lineCode == "GT") "KI" else line.lineCode
                Trip(
                    id = line.tripNumber,
                    code = lineCode,
                    name = line.lineName,
                    destination = line.directionName.split(" - ").last(),
                    color = lineColours[lineCode] ?: Color.Gray,
                    tripOrder = line.tripOrder,
                    lastUpdated = Instant.parse(lastUpdated, inFormatter),
                    isCancelled = line.tripNumber in cancelledTrips,
                    departureTime = departureTime,
                    platform = platform,
                    isBus = line.serviceType == "B",
                )
            }?.sortedBy { it.departureTime } ?: emptyList()
            if (stationCode == "UN") {
                val unionDepartures = departureScreenAPI.getUnionDepartures()
                val tripsMap = unionDepartures.allDepartures?.trips?.associateBy { it.tripNumber } ?: emptyMap()
                result.mapNotNull { train ->
                    tripsMap[train.id]?.let { matchingTrip ->
                        train.copy(
                            info = matchingTrip.info,
                            platform = matchingTrip.platform,
                            departureTime = Instant.parse(matchingTrip.time, inFormatter),
                        )
                    }
                }
            } else {
                result
            }
        } catch (exception: IOException) {
            throw exception
        } catch (exception: Exception) {
            exception.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getServiceAlerts(): List<Alert> = processAlerts(departureScreenAPI.getServiceAlerts())

    override suspend fun getInformationAlerts(): List<Alert> = processAlerts(departureScreenAPI.getInfromationAlerts())

    override suspend fun getAllStations(): List<Station> {
        return try {
            departureScreenAPI.getAllStops().stations?.stops?.map { stop ->
                Station(
                    name = stop.locationName,
                    code = stop.locationCode,
                    type = stop.locationType,
                )
            } ?: emptyList()
        } catch (exception: IOException) {
            throw exception
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun processAlerts(alerts: Alerts): List<Alert> {
        return try {
            alerts.messages?.message?.map { message ->
                Alert(
                    id = message.code,
                    date = Instant.parse(message.postedDateTime, inFormatter),
                    affectedLines = message.lines.map { it.code },
                    affectedStations = message.stops.map { it.code },
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

fun Instant.Companion.parseOrNull(input: CharSequence, format: DateTimeFormat<DateTimeComponents>): Instant? {
    return try {
        Instant.parse(input, format)
    } catch (_: Exception) {
        null
    }
}