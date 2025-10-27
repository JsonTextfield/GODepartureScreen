package com.jsontextfield.departurescreen.core.data

import androidx.compose.ui.graphics.Color
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.core.network.model.Alerts
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
    private var stations = emptyList<Station>()

    override suspend fun getTrains(stationCode: String): List<Trip> {
        return try {
            val nextService = departureScreenAPI.getNextService(stationCode)
            val exceptions = departureScreenAPI.getExceptions()
            val lastUpdated = Instant.parse(nextService.metadata?.timestamp.orEmpty(), inFormatter)
            val lines = nextService.nextService?.lines
            val cancelledTrips = exceptions.trip.filter { it.isCancelled == "1" }.map { it.tripNumber }

            val tripsMap = if (stationCode == "UN") {
                val unionDepartures = departureScreenAPI.getUnionDepartures()
                unionDepartures.allDepartures?.trips?.associateBy { it.tripNumber } ?: emptyMap()
            } else {
                emptyMap()
            }

            lines?.map { line ->
                val trip = tripsMap[line.tripNumber]
                val departureTime = (trip?.time ?: line.computedDepartureTime ?: line.scheduledDepartureTime)?.let {
                    Instant.parseOrNull(it, inFormatter)
                } ?: Instant.fromEpochMilliseconds(0)
                val platform = trip?.platform ?: line.actualPlatform.takeIf { !it.isNullOrBlank() }
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
                    lastUpdated = lastUpdated,
                    isCancelled = line.tripNumber in cancelledTrips,
                    departureTime = departureTime,
                    platform = platform,
                    isBus = line.serviceType == "B",
                    info = trip?.info.orEmpty(),
                )
            }?.sortedBy { it.departureTime } ?: emptyList()
        } catch (exception: IOException) {
            throw exception
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getServiceAlerts(): List<Alert> = processAlerts(departureScreenAPI.getServiceAlerts())

    override suspend fun getInformationAlerts(): List<Alert> = processAlerts(departureScreenAPI.getInfromationAlerts())

    override suspend fun getAllStations(): List<Station> {
        if (stations.isNotEmpty()) {
            return stations
        }
        return try {
            departureScreenAPI.getAllStops().stations?.stops?.map { stop ->
                Station(
                    name = stop.locationName,
                    code = stop.locationCode,
                    type = stop.locationType,
                )
            }?.groupBy { it.name }
                ?.map { (stationName: String, stations: List<Station>) ->
                    Station(
                        name = stationName,
                        code = stations.map { it.code }.toSet().joinToString(","),
                        type = stations.map { it.type }.toSet().joinToString(","),
                        isFavourite = stations.any { it.isFavourite },
                    )
                } ?: emptyList()
        } catch (exception: IOException) {
            throw exception
        } catch (_: Exception) {
            emptyList()
        }.also {
            stations = it
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