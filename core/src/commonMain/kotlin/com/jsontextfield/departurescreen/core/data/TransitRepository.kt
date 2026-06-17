@file:OptIn(ExperimentalTime::class)

package com.jsontextfield.departurescreen.core.data

import androidx.compose.ui.graphics.Color
import co.touchlab.kermit.Logger
import com.jsontextfield.departurescreen.core.entities.Alert
import com.jsontextfield.departurescreen.core.entities.Schedule
import com.jsontextfield.departurescreen.core.entities.Stop
import com.jsontextfield.departurescreen.core.entities.Trip
import com.jsontextfield.departurescreen.core.entities.TripDetails
import com.jsontextfield.departurescreen.core.network.DepartureScreenAPI
import com.jsontextfield.departurescreen.core.network.model.AlertsResponse
import com.jsontextfield.departurescreen.core.network.model.ExceptionsResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceAtAGlanceTrainsResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceUpdatesResponse
import com.jsontextfield.departurescreen.core.network.model.StopResponse
import com.jsontextfield.departurescreen.core.ui.StopType
import com.jsontextfield.departurescreen.core.ui.theme.lineColours
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.io.IOException
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

const val UP_EXPRESS_STOPS = "UN,BL,MD,WE,PA"
const val UP_EXPRESS = "UP Express"

class TransitRepository(
    private val departureScreenAPI: DepartureScreenAPI
) : ITransitRepository {
    private val timeZone = TimeZone.of("America/Toronto")
    private var stops: List<Stop> = emptyList()
    private var serviceAlerts: List<Alert> = emptyList()
    private var informationAlerts: List<Alert> = emptyList()
    private var marketingAlerts: List<Alert> = emptyList()

    private var lastUpdated: Long = 0L
    private var serviceAtAGlanceTrains: Map<String, ServiceAtAGlanceTrainsResponse.Trips.Trip>? = null
    private var exceptions: ExceptionsResponse? = null

    override suspend fun getTrips(stopCode: String): List<Trip> {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        if (currentTime - lastUpdated > 60_000) {
            serviceAtAGlanceTrains =
                departureScreenAPI.getServiceAtAGlanceTrains().trips?.trip?.associateBy { it.tripNumber }
            exceptions = departureScreenAPI.getAllExceptions()
            lastUpdated = currentTime
        }
        return try {
            val nextService = departureScreenAPI.getNextService(stopCode)
            val lastUpdated = LocalDateTime.parse(nextService.metadata?.timestamp.orEmpty(), inFormatter).toInstant(timeZone)
            val lines = nextService.nextService?.lines
            val cancelledTrips =
                exceptions?.trip?.filter { it.isCancelled == "1" }?.map { it.tripNumber } ?: emptyList()

            val tripsMap = if (stopCode == "UN") {
                val unionDepartures = departureScreenAPI.getUnionDepartures()
                unionDepartures.allDepartures?.trips?.associateBy { it.tripNumber } ?: emptyMap()
            } else {
                emptyMap()
            }

            val upExpressTrips: List<Trip> = if (stopCode in UP_EXPRESS_STOPS) {
                departureScreenAPI.getUpGtfsTripUpdates().entity.mapNotNull { entity ->
                    entity.tripUpdate?.stopTimeUpdate?.firstOrNull {
                        it.stopId == stopCode
                    }?.departure?.time?.let { departureTimeSeconds: Long ->
                        val departureTime = Instant
                            .fromEpochSeconds(departureTimeSeconds)
                            .toLocalDateTime(timeZone)
                            .toInstant(TimeZone.UTC)
                        val trip = entity.tripUpdate.trip
                        val vehicle = entity.tripUpdate.vehicle
                        Trip(
                            id = trip.tripId,
                            code = trip.routeId,
                            name = UP_EXPRESS,
                            destination = vehicle?.label?.split(" - ")?.last().orEmpty(),
                            color = lineColours[trip.routeId] ?: Color.Gray,
                            lastUpdated = lastUpdated.toLocalDateTime(timeZone).toInstant(TimeZone.UTC),
                            isCancelled = trip.tripId in cancelledTrips,
                            departureTime = departureTime,
                            platform = "-",
                        )
                    }
                }
            } else {
                emptyList()
            }

            val trips = lines?.map { line ->
                val trip = tripsMap[line.tripNumber]
                val departureTime = (trip?.time ?: line.computedDepartureTime ?: line.scheduledDepartureTime)?.let {
                    LocalDateTime.parse(it, inFormatter).toInstant(TimeZone.UTC)
                } ?: Instant.fromEpochMilliseconds(0)
                val platform = trip?.platform ?: line.actualPlatform.takeIf { !it.isNullOrBlank() }
                ?: line.scheduledPlatform.takeIf { !it.isNullOrBlank() }
                ?: "-"

                val lineCode = if (line.lineCode == "GT") "KI" else line.lineCode
                val updateTime = LocalDateTime.parse(line.updateTime, inFormatter).toInstant(TimeZone.UTC) ?: lastUpdated
                Trip(
                    id = line.tripNumber,
                    code = lineCode,
                    name = line.lineName,
                    destination = line.directionName.split(" - ").last(),
                    color = lineColours[lineCode] ?: Color.Gray,
                    tripOrder = line.tripOrder,
                    lastUpdated = updateTime,
                    isCancelled = line.tripNumber in cancelledTrips,
                    departureTime = departureTime,
                    platform = platform,
                    isBus = line.serviceType == "B",
                    info = trip?.info.orEmpty(),
                    cars = serviceAtAGlanceTrains?.get(line.tripNumber)?.cars,
                    //busType = serviceAtAGlanceBuses?.get(line.tripNumber)?.busType,
                )
            } ?: emptyList()
            (upExpressTrips + trips).sortedBy { it.departureTime }
        } catch (exception: IOException) {
            throw exception
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getUPExpressTripSchedule(id: String): List<Schedule> {
        val lastUpdated = Clock.System.now()
            .toLocalDateTime(timeZone)
            .toInstant(TimeZone.UTC)
        return try {
            val allStops = getAllStops().associate { it.code to it.name }
            departureScreenAPI.getUpGtfsTripUpdates().entity.firstOrNull { entity ->
                entity.id == id
            }?.let { entity ->
                entity.tripUpdate?.stopTimeUpdate?.map { stopTimeUpdate ->
                    val departureTime = Instant
                        .fromEpochSeconds(stopTimeUpdate.departure?.time ?: 0L)
                        .toLocalDateTime(timeZone)
                        .toInstant(TimeZone.UTC)
                    Schedule(
                        name = allStops[stopTimeUpdate.stopId]
                            ?: allStops.firstNotNullOfOrNull { (code, name) ->
                                if (stopTimeUpdate.stopId in code) name else null
                            }
                            ?: "",
                        code = stopTimeUpdate.stopId,
                        time = departureTime,
                        lastUpdated = lastUpdated,
                    )
                }
            } ?: emptyList()
        } catch (_: IOException) {
            emptyList()
        }
    }

    override suspend fun getTripDetails(tripNumber: String): TripDetails? {
        return try {
            val serviceDate = (Clock.System.now() - 5.hours).toLocalDateTime(timeZone).date
            val serviceMidnight = LocalDateTime(serviceDate, LocalTime(0, 0)).toInstant(TimeZone.UTC)

            val tripDetailsResponse = departureScreenAPI.getTrip(tripNumber, serviceDate.toString().replace("-", ""))
            val serviceGuaranteeResponse = departureScreenAPI.getServiceGuarantee(tripNumber)
            val stops = serviceGuaranteeResponse.stops?.stop.orEmpty()
            val allStops = getAllStops().associate { it.code to it.name }
            val lastUpdated = LocalDateTime
                .parse(tripDetailsResponse.metadata?.timestamp.orEmpty(), inFormatter)
                .toInstant(timeZone)
            tripDetailsResponse.trips.map { trip ->
                TripDetails(
                    id = trip.tripNumber,
                    stops = trip.stops.map { stop ->
                        val time = stop.arrivalTime?.computed.takeIf { !it.isNullOrBlank() }
                            ?: stop.arrivalTime?.scheduled.takeIf { !it.isNullOrBlank() }
                            ?: stop.departureTime?.computed.takeIf { !it.isNullOrBlank() }
                            ?: stop.departureTime?.scheduled.takeIf { !it.isNullOrBlank() }

                        val stopTime = time?.let {
                            val colonIndex = it.indexOf(':')
                            if (colonIndex != -1) {
                                val h = it.substring(0, colonIndex).toLongOrNull() ?: 0L
                                val m = it.substring(colonIndex + 1).toLongOrNull() ?: 0L
                                val days = if (h < 5) 1 else 0
                                serviceMidnight + days.days + h.hours + m.minutes
                            } else null
                        }
                        Schedule(
                            name = allStops[stop.code]
                                ?: allStops.firstNotNullOfOrNull { (code, name) -> if (stop.code in code) name else null }
                                ?: "",
                            code = stop.code,
                            time = stopTime,
                            lastUpdated = lastUpdated.toLocalDateTime(timeZone).toInstant(TimeZone.UTC),
                        )
                    }.sortedBy { stop ->
                        stop.time
                    },
                    serviceGuarantee = stops.joinToString("\n"),
                )
            }.firstOrNull()
        } catch (e: Exception) {
            Logger.withTag(TransitRepository::class.simpleName.toString()).e(e.message.orEmpty())
            null
        }
    }

    override fun getServiceAlerts(): Flow<List<Alert>> = pollAlerts(
        apiCall = { departureScreenAPI.getServiceAlerts() },
        getCache = { serviceAlerts },
        updateCache = { serviceAlerts = it }
    )

    override fun getInformationAlerts(): Flow<List<Alert>> = pollAlerts(
        apiCall = { departureScreenAPI.getInformationAlerts() },
        getCache = { informationAlerts },
        updateCache = { informationAlerts = it }
    )

    override fun getMarketingAlerts(): Flow<List<Alert>> = pollAlerts(
        apiCall = { departureScreenAPI.getMarketingAlerts() },
        getCache = { marketingAlerts },
        updateCache = { marketingAlerts = it }
    )

    override fun getServiceUpdates(type: String, language: String): Flow<List<Alert>> = flow {
        while (currentCoroutineContext().isActive) {
            try {
                val response = departureScreenAPI.getServiceUpdates(type, language)
                emit(processServiceUpdates(response))
            } catch (exception: Exception) {
                Logger.withTag(TransitRepository::class.simpleName.toString()).e { exception.message.toString() }
                emit(emptyList())
            }
            delay(60.seconds)
        }
    }

    private fun pollAlerts(
        apiCall: suspend () -> AlertsResponse,
        getCache: () -> List<Alert>,
        updateCache: (List<Alert>) -> Unit
    ): Flow<List<Alert>> = flow {
        while (currentCoroutineContext().isActive) {
            try {
                updateCache(
                    apiCall().toAlerts().also { emit(it) }
                )
            } catch (exception: Exception) {
                Logger.withTag(TransitRepository::class.simpleName.toString()).e { exception.message.toString() }
                emit(getCache())
            }
            delay(60.seconds)
        }
    }

    override suspend fun getAllStops(): List<Stop> {
        if (stops.isNotEmpty()) {
            return stops
        }
        return try {
            departureScreenAPI.getAllStops().stations?.stops?.groupBy {
                it.locationName
            }?.map { (stopName: String, stops: List<StopResponse.Stations.Stop>) ->
                val types = buildSet {
                    for (stop in stops) {
                        // Possible stop types: [Bus Stop, Bus Terminal, Park & Ride, Train & Bus Station, Train Station]
                        if (StopType.TRAIN.typeString in stop.locationType && StopType.BUS.typeString in stop.locationType) {
                            // Train & Bus Station
                            add(StopType.TRAIN)
                            add(StopType.BUS)
                        } else if (StopType.TRAIN.typeString in stop.locationType) {
                            // Train Station
                            add(StopType.TRAIN)
                        } else if (StopType.BUS.typeString in stop.locationType) {
                            // Bus Stop, Bus Terminal
                            add(StopType.BUS)
                        } else {
                            // Park & Ride
                            add(StopType.BUS)
                        }
                    }
                }
                Stop(
                    name = stopName,
                    code = stops.joinToString(",") { it.locationCode },
                    types = types,
                )
            } ?: emptyList()
        } catch (exception: IOException) {
            throw exception
        } catch (_: Exception) {
            emptyList()
        }.also {
            stops = it
        }
    }

    private fun AlertsResponse.toAlerts(): List<Alert> {
        val allStops = stops.associate {
            it.code to it.name
        }
        return try {
            messages?.message?.map { message ->
                val bodyEn = message.bodyEnglish.orEmpty().trim()
                val bodyFr = message.bodyFrench.orEmpty().trim()
                val date = LocalDateTime
                    .parse(message.postedDateTime, inFormatter)
                    .toInstant(timeZone)
                Alert(
                    id = message.code,
                    date = date,
                    affectedLines = message.lines.map { if (it.code == "GT") "KI" else it.code },
                    affectedStops = allStops.mapNotNull {
                        if (message.stops.any { stop -> stop.code in it.key }) {
                            it.value
                        } else {
                            null
                        }
                    },
                    subjectEn = message.subjectEnglish.orEmpty().trim(),
                    subjectFr = message.subjectFrench.orEmpty().trim(),
                    bodyEn = bodyEn,
                    bodyFr = bodyFr,
                )
            }?.sortedByDescending { it.date } ?: emptyList()
        } catch (exception: IOException) {
            throw exception
        } catch (exception: Exception) {
            exception.printStackTrace()
            emptyList()
        }
    }

    private fun processServiceUpdates(response: ServiceUpdatesResponse): List<Alert> {
        val alerts = mutableListOf<Alert>()

        // Process top-level notifications
        response.notifications?.notification?.let {
            alerts.addAll(it.map { n -> n.toAlert() })
        }

        // Process train-specific notifications
        response.trains?.train?.forEach { train ->
            train.notifications?.notification?.forEach { n ->
                alerts.add(n.toAlert(affectedLines = listOfNotNull(train.corridorCode)))
            }
        }

        // Process bus-specific notifications
        response.buses?.bus?.forEach { bus ->
            bus.notifications?.notification?.forEach { n ->
                alerts.add(n.toAlert(affectedLines = listOfNotNull(bus.routeNumber)))
            }
        }

        // Process station-specific notifications
        response.stations?.station?.forEach { station ->
            station.notifications?.notification?.forEach { n ->
                alerts.add(n.toAlert(affectedStops = listOfNotNull(station.stationName)))
            }
        }

        // Process announcements
        response.trainAnnouncements?.notification?.forEach { n ->
            alerts.add(n.toAlert())
        }
        response.busAnnouncements?.notification?.forEach { n ->
            alerts.add(n.toAlert(affectedLines = listOfNotNull(n.code)))
        }

        return alerts.distinctBy { it.id }.sortedByDescending { it.date }
    }

    private fun ServiceUpdatesResponse.Notification.toAlert(
        affectedLines: List<String> = emptyList(),
        affectedStops: List<String> = emptyList()
    ): Alert {
        val subject = messageSubject.orEmpty()
        val body = messageBody.orEmpty()
        val dateStr = postedDateTime.orEmpty()
        val date = try {
            LocalDateTime
                .parse(dateStr, serviceUpdateDateFormatter)
                .toInstant(timeZone)
        } catch (_: Exception) {
            Instant.fromEpochMilliseconds(0)
        }

        val finalLines = affectedLines.map {
            if (it == "GT") "KI" else it
        }

        return Alert(
            id = code ?: (dateStr + subject).hashCode().toString(),
            date = date,
            affectedLines = finalLines,
            affectedStops = affectedStops,
            subjectEn = subject,
            subjectFr = subject,
            bodyEn = body,
            bodyFr = body,
        )
    }

    companion object {
        @OptIn(FormatStringsInDatetimeFormats::class)
        val inFormatter = LocalDateTime.Format {
            year()
            char('-')
            monthNumber()
            char('-')
            day()
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }

        @OptIn(FormatStringsInDatetimeFormats::class)
        val serviceUpdateDateFormatter = LocalDateTime.Format {
            monthNumber()
            char('/')
            day()
            char('/')
            year()
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    }
}