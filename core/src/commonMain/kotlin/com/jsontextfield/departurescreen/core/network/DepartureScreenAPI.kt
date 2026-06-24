@file:OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)

package com.jsontextfield.departurescreen.core.network

import com.jsontextfield.departurescreen.core.network.model.AlertsResponse
import com.jsontextfield.departurescreen.core.network.model.ExceptionsResponse
import com.jsontextfield.departurescreen.core.network.model.JourneyResponse
import com.jsontextfield.departurescreen.core.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceAtAGlanceBusesResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceAtAGlanceTrainsResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceGuaranteeResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceUpdatesResponse
import com.jsontextfield.departurescreen.core.network.model.StopResponse
import com.jsontextfield.departurescreen.core.network.model.TripResponse
import com.jsontextfield.departurescreen.core.network.model.TripUpdatesResponse
import com.jsontextfield.departurescreen.core.network.model.UnionDeparturesResponse
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import co.touchlab.kermit.Logger as Kermit


class DepartureScreenAPI {
    companion object {
        private const val BASE_HOST = "api.openmetrolinx.com"
        private const val BASE_HOST2 = "api.metrolinx.com"
        private const val V1_API_PATH = "/OpenDataAPI/api/V1/"
        private const val DEFAULT_TIMEOUT_MS = 12000L
    }

    private val json = Json {
        prettyPrint = false
        isLenient = true
        ignoreUnknownKeys = true
    }
    private val httpLogger = object : Logger {
        override fun log(message: String) {
            Kermit.withTag("HttpClient").d(message)
        }
    }

    private fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}) = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = httpLogger
            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            requestTimeoutMillis = DEFAULT_TIMEOUT_MS
            connectTimeoutMillis = DEFAULT_TIMEOUT_MS
            socketTimeoutMillis = DEFAULT_TIMEOUT_MS
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = BASE_HOST
            }
        }
        this.block()
    }

    private val client = createHttpClient {
        defaultRequest {
            url {
                host = BASE_HOST
                encodedPath = V1_API_PATH
                parameters.append("key", API_KEY)
            }
        }
    }

    private val serviceUpdatesClient = createHttpClient {
        defaultRequest {
            url {
                host = BASE_HOST2
            }
        }
    }

    // UPGTFSRealTimeV1
    suspend fun getUpGtfsAlerts(): String {
        return client.get("UP/Gtfs/Feed/Alerts").body()
    }

    suspend fun getUpGtfsTripUpdates(): TripUpdatesResponse {
        return client.get("UP/Gtfs/Feed/TripUpdates").body()
    }

    suspend fun getUpGtfsVehiclePositions(): String {
        return client.get("UP/Gtfs/Feed/VehiclePosition").body()
    }

    // Stop
    suspend fun getNextService(stopCode: String): NextServiceResponse {
        return client.get("Stop/NextService/$stopCode").body()
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
    suspend fun getServiceAlerts(): AlertsResponse {
        return client.get("ServiceUpdate/ServiceAlert/All").body()
    }

    suspend fun getInformationAlerts(): AlertsResponse {
        return client.get("ServiceUpdate/InformationAlert/All").body()
    }

    suspend fun getMarketingAlerts(): AlertsResponse {
        return client.get("ServiceUpdate/MarketingAlert/All").body()
    }

    suspend fun getUnionDepartures(): UnionDeparturesResponse {
        return client.get("ServiceUpdate/UnionDepartures/All").body()
    }

    suspend fun getServiceGuarantee(tripNumber: String): ServiceGuaranteeResponse {
        val date = (Clock.System.now() - 8.hours).format(
            DateTimeComponents.Format {
                year()
                monthNumber()
                day()
            }
        )
        return client.get("ServiceUpdate/ServiceGuarantee/$tripNumber/$date").body()
        //return getServiceGuaranteeSample()
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

    /**
     * This endpoint is an alternative to the ServiceAlert/All, InformationAlert/All, and MarketingAlert/All endpoints.
     * Instead of calling those endpoints, this one can be called.
     * @param type type of service update. Valid values are "general" and "all"
     * @param language language code for the response. Valid values are "en" and "fr"
     */
    suspend fun getServiceUpdates(type: String, language: String): ServiceUpdatesResponse {
        val jsonString = serviceUpdatesClient.get("/external/go/serviceupdate/$language/$type").bodyAsText()
        return json.decodeFromString<ServiceUpdatesResponse>(jsonString)
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
    ): JourneyResponse {
        return client.get("Schedule/Journey/$date/$from/$to/$startTime/$maxJourney").body()
    }

    suspend fun getJourney(
        date: String,
        from: String,
        startTime: String,
        maxJourney: Int,
        to: String? = null,
    ): JourneyResponse {
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

    suspend fun getTrip(tripNumber: String, date: String): TripResponse {
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

    // Sample function that returns ServiceGuaranteeResponse with the provided JSON structure
    fun getServiceGuaranteeSample(): ServiceGuaranteeResponse {
        val jsonString = """
            {
              "Metadata": {
                "TimeStamp": "sample string 1",
                "ErrorCode": "sample string 2",
                "ErrorMessage": "sample string 3"
              },
              "Stops": {
                "Stop": [
                  {
                    "Code": "sample string 1",
                    "Scope": "sample string 2",
                    "ReasonEn": "sample string 3",
                    "ReasonFr": "sample string 4"
                  },
                  {
                    "Code": "sample string 1",
                    "Scope": "sample string 2",
                    "ReasonEn": "sample string 3",
                    "ReasonFr": "sample string 4"
                  }
                ]
              }
            }
        """.trimIndent()
        return json.decodeFromString<ServiceGuaranteeResponse>(jsonString)
    }
}