@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.data

import androidx.compose.ui.graphics.Color
import co.touchlab.kermit.Logger
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Station
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.core.network.model.Alerts
import com.jsontextfield.departurescreen.core.network.model.ExceptionsResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceAtAGlanceTrainsResponse
import com.jsontextfield.departurescreen.core.network.model.StopResponse
import com.jsontextfield.departurescreen.core.ui.StationType
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.parse
import kotlinx.io.IOException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalSerializationApi::class)
class GoTrainDataSource(
    private val departureScreenAPI: DepartureScreenAPI
) : IGoTrainDataSource {
    private var stations = emptyList<Station>()

    private var lastUpdated: Long = 0L
    private var serviceAtAGlanceTrains: Map<String, ServiceAtAGlanceTrainsResponse.Trips.Trip>? = null
    private var exceptions: ExceptionsResponse? = null

    override suspend fun getTrips(stationCode: String): List<Trip> {
        if (Clock.System.now().toEpochMilliseconds() - lastUpdated > 60_000) {
            serviceAtAGlanceTrains =
                departureScreenAPI.getServiceAtAGlanceTrains().trips?.trip?.associateBy { it.tripNumber }
            exceptions = departureScreenAPI.getExceptions()
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        }
        return try {
            val nextService = departureScreenAPI.getNextService(stationCode)
            val lastUpdated = Instant.parse(nextService.metadata?.timestamp.orEmpty(), inFormatter)
            val lines = nextService.nextService?.lines
            val cancelledTrips =
                exceptions?.trip?.filter { it.isCancelled == "1" }?.map { it.tripNumber } ?: emptyList()

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
                    cars = serviceAtAGlanceTrains?.get(line.tripNumber)?.cars,
                    //busType = serviceAtAGlanceBuses?.get(line.tripNumber)?.busType,
                )
            }?.sortedBy { it.departureTime } ?: emptyList()
        } catch (exception: IOException) {
            throw exception
        } catch (_: Exception) {
            emptyList()
        }
    }

    override fun getServiceAlerts(): Flow<List<Alert>> = flow {
        while (currentCoroutineContext().isActive) {
            try {
                emit(processAlerts(departureScreenAPI.getServiceAlerts()))
            } catch (exception: Exception) {
                Logger.withTag(GoTrainDataSource::class.simpleName.toString()).e { exception.message.toString() }
            }
            delay(1.minutes)
        }
    }

    override fun getInformationAlerts(): Flow<List<Alert>> = flow {
        while (currentCoroutineContext().isActive) {
            try {
                emit(processAlerts(departureScreenAPI.getInfromationAlerts()))
            } catch (exception: Exception) {
                Logger.withTag(GoTrainDataSource::class.simpleName.toString()).e { exception.message.toString() }
            }
            delay(1.minutes)
        }
    }

    override suspend fun getAllStations(): List<Station> {
        if (stations.isNotEmpty()) {
            return stations
        }
        return try {
            departureScreenAPI.getAllStops().stations?.stops?.groupBy {
                it.locationName
            }?.map { (stationName: String, stations: List<StopResponse.Stations.Stop>) ->
                val types = buildSet {
                    stations.forEach {
                        if ("Bus" in it.locationType) {
                            add(StationType.BUS)
                        }
                        if ("Train" in it.locationType) {
                            add(StationType.TRAIN)
                        }
                    }
                }
                Station(
                    name = stationName,
                    code = stations.joinToString(",") { it.locationCode },
                    types = types,
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
        val allStations = stations.associate {
            it.code to it.name
        }
        return try {
            alerts.messages?.message?.map { message ->
                Alert(
                    id = message.code,
                    date = Instant.parse(message.postedDateTime, inFormatter),
                    affectedLines = message.lines.map { if (it.code == "GT") "KI" else it.code },
                    affectedStations = allStations.mapNotNull {
                        if (message.stops.any { stop -> stop.code in it.key }) {
                            it.value
                        } else {
                            null
                        }
                    },
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