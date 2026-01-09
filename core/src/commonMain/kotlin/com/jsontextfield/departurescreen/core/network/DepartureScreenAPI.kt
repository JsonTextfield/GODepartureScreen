package com.jsontextfield.departurescreen.core.network

import com.jsontextfield.departurescreen.core.network.model.Alerts
import com.jsontextfield.departurescreen.core.network.model.ExceptionsResponse
import com.jsontextfield.departurescreen.core.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceAtAGlanceBusesResponse
import com.jsontextfield.departurescreen.core.network.model.ServiceAtAGlanceTrainsResponse
import com.jsontextfield.departurescreen.core.network.model.StopResponse
import com.jsontextfield.departurescreen.core.network.model.UnionDeparturesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

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
            logger = Logger.SIMPLE
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

    suspend fun getAllStops(): StopResponse {
        return client.get("Stop/All").body()
    }

    suspend fun getNextService(stationCode: String): NextServiceResponse {
        return client.get("Stop/NextService/$stationCode").body()
    }

    suspend fun getUnionDepartures(): UnionDeparturesResponse {
        return client.get("ServiceUpdate/UnionDepartures/All").body()
    }

    suspend fun getServiceAtAGlanceTrains(): ServiceAtAGlanceTrainsResponse {
        return client.get("ServiceataGlance/Trains/All").body()
    }

    suspend fun getServiceAtAGlanceBuses(): ServiceAtAGlanceBusesResponse {
        return client.get("ServiceataGlance/Buses/All").body()
    }

    suspend fun getServiceAlerts(): Alerts {
        return client.get("ServiceUpdate/ServiceAlert/All").body()
    }

    suspend fun getInformationAlerts(): Alerts {
        return client.get("ServiceUpdate/InformationAlert/All").body()
    }

    suspend fun getExceptions(): ExceptionsResponse {
        return client.get("ServiceUpdate/Exceptions/Train").body()
    }
}