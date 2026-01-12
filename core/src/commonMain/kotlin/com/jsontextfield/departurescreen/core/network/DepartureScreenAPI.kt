package com.jsontextfield.departurescreen.core.network

import com.jsontextfield.departurescreen.core.network.model.Alerts
import com.jsontextfield.departurescreen.core.network.model.ExceptionsResponse
import com.jsontextfield.departurescreen.core.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceAtAGlanceBusesResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceAtAGlanceTrainsResponse
import com.jsontextfield.departurescreen.core.network.model.StopResponse
import com.jsontextfield.departurescreen.core.network.model.TripResponse
import com.jsontextfield.departurescreen.core.network.model.UnionDeparturesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import co.touchlab.kermit.Logger as Kermit

@OptIn(ExperimentalSerializationApi::class)
class DepartureScreenAPI() {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Kermit
                        .withTag("HttpClient")
                        .d(message)
                }
            }
            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 6000
            connectTimeoutMillis = 6000
            socketTimeoutMillis = 6000
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.openmetrolinx.com"
                encodedPath = "/OpenDataAPI/api/V1/"
                parameters.append("key", API_KEY)
            }
        }
    }

    // UPGTFSRealTimeV1
    suspend fun getUpGtfsAlerts(): String {
        return client.get("UP/Gtfs/Feed/Alerts").body()
    }

    suspend fun getUpGtfsTripUpdates(): String {
        return client.get("UP/Gtfs/Feed/TripUpdates").body()
    }

    suspend fun getUpGtfsVehiclePositions(): String {
        return client.get("UP/Gtfs/Feed/VehiclePosition").body()
    }

    // Stop
    suspend fun getNextService(stationCode: String): NextServiceResponse {
        return client.get("Stop/NextService/$stationCode").body()
    }
    suspend fun getStopDetails(stopCode: String): String {
        return client.get("Stop/Details/$stopCode").body()
    }
    suspend fun getStopDestinations(
        stopCode: String,
        from: String,
        to: String
    ): String {
        return client.get("Stop/Destinations/$stopCode/$from/$to").body()
    }
    suspend fun getAllStops(): StopResponse {
        return client.get("Stop/All").body()
    }

    // Service Update
    suspend fun getServiceAlerts(): Alerts {
        return client.get("ServiceUpdate/ServiceAlert/All").body()
    }
    suspend fun getInformationAlerts(): Alerts {
        return client.get("ServiceUpdate/InformationAlert/All").body()
    }
    suspend fun getMarketingAlerts(): Alerts {
        return client.get("ServiceUpdate/MarketingAlert/All").body()
    }
    suspend fun getUnionDepartures(): UnionDeparturesResponse {
        return client.get("ServiceUpdate/UnionDepartures/All").body()
    }
    suspend fun getTrainExceptions(): ExceptionsResponse {
        return client.get("ServiceUpdate/Exceptions/Train").body()
    }
    suspend fun getBusExceptions(): ExceptionsResponse {
        return client.get("ServiceUpdate/Exceptions/Bus").body()
    }
    suspend fun getAllExceptions(): ExceptionsResponse {
        return client.get("ServiceUpdate/Exceptions/All").body()
    }

    // Service At Glance
    suspend fun getServiceAtAGlanceTrains(): ServiceAtAGlanceTrainsResponse {
        return client.get("ServiceataGlance/Trains/All").body()
    }
    suspend fun getServiceAtAGlanceBuses(): ServiceAtAGlanceBusesResponse {
        return client.get("ServiceataGlance/Buses/All").body()
    }

    // Schedule
    suspend fun getJourney(
        date: String,
        from: String,
        to: String,
        startTime: String,
        maxJourney: Int,
    ): String {
        return client.get("Schedule/Journey/$date/$from/$to/$startTime/$maxJourney").body()
    }
    suspend fun getJourney(
        date: String,
        from: String,
        startTime: String,
        maxJourney: Int,
        to: String? = null,
    ): String {
        return client.get("Schedule/Journey/$date/$from/$startTime/$maxJourney") {
            to?.let {
                url {
                    parameters.append("ToStopCode", to)
                }
            }
        }.body()
    }
    suspend fun getLine(
        date: String,
        code: String,
        direction: String,
    ): String {
        return client.get("Schedule/Line/$date/$code/$direction").body()
    }
    @OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)
    suspend fun getTrip(tripNumber: String): TripResponse {
        val date = (Clock.System.now() - 8.hours).format(
            DateTimeComponents.Format {
                byUnicodePattern("yyyyMMdd")
            }
        )
        return client.get("Schedule/Trip/$date/$tripNumber").body()
    }

    // GTFS Feeds
    suspend fun getGtfsAlerts(): String {
        return client.get("Gtfs/Feed/Alerts").body()
    }
    suspend fun getGtfsTripUpdates(): String {
        return client.get("Gtfs/Feed/TripUpdates").body()
    }
    suspend fun getGtfsVehiclePositions(): String {
        return client.get("Gtfs/Feed/VehiclePosition").body()
    }

    // Fare
    suspend fun getFares(
        from: String,
        to: String,
    ): String {
        return client.get("Fares/$from/$to").body()
    }
    suspend fun getFares(
        from: String,
        to: String,
        date: String,
    ): String {
        return client.get("Fares/$from/$to/$date").body()
    }
}