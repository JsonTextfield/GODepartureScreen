package com.jsontextfield.departurescreen.network

import com.jsontextfield.departurescreen.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.network.model.UnionDeparturesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object DepartureScreenAPI {
    private val client by lazy {
        HttpClient {
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
                level = LogLevel.HEADERS
            }
            defaultRequest {
                url("https://api.openmetrolinx.com/OpenDataAPI/")
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun getNextService(apiKey: String): NextServiceResponse {
        return client.get {
            url {
                path("api/V1/Stop/NextService/UN")
                parameter("key", apiKey)
            }
        }.body()
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun getUnionDepartures(apiKey: String): UnionDeparturesResponse {
        return client.get {
            url {
                path("api/V1/ServiceUpdate/UnionDepartures/All")
                parameter("key", apiKey)
            }
        }.body()
    }
}