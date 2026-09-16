package com.jsontextfield.departurescreen.core.network

import co.touchlab.kermit.Logger
import com.jsontextfield.departurescreen.core.network.model.FeatureFlagsResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FeatureFlagApi {
    private val json =
        Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    private val httpClient = HttpClient {
        install(HttpCache)
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Logger
                        .withTag("HttpClient")
                        .d(message)
                }
            }
            level = LogLevel.INFO
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 12000
            connectTimeoutMillis = 12000
            socketTimeoutMillis = 12000
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = "xshrnsirslihjdxkfhfl.supabase.co"
                encodedPath = "/rest/v1/"
                parameters.append("apikey", SUPABASE_API_KEY)
            }
        }
    }

    suspend fun getFeatureFlags() : List<FeatureFlagsResponse> {
        val result = httpClient.get {
            url.path("feature_flags")
            url.parameters.append("app", "eq.departures")
        }.bodyAsText()
        return json.decodeFromString<List<FeatureFlagsResponse>>(result)
    }
}

expect suspend fun isAdEnabled(featureFlagApi: FeatureFlagApi): Boolean