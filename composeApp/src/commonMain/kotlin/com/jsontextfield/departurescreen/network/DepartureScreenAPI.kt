package com.jsontextfield.departurescreen.network

import com.jsontextfield.departurescreen.network.model.Alerts
import com.jsontextfield.departurescreen.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.network.model.UnionDeparturesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.path
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class DepartureScreenAPI(private val client: HttpClient) {
    suspend fun getNextService(): NextServiceResponse {
        return client.get {
            url {
                path("Stop/NextService/UN")
                parameter("key", API_KEY)
            }
        }.body()
    }

    suspend fun getUnionDepartures(): UnionDeparturesResponse {
        return client.get {
            url {
                path("ServiceUpdate/UnionDepartures/All")
                parameter("key", API_KEY)
            }
        }.body()
    }

    suspend fun getServiceAlerts(): Alerts {
        return client.get {
            url {
                path("ServiceUpdate/ServiceAlert/All")
                parameter("key", API_KEY)
            }
        }.body()
    }

    suspend fun getInfromationAlerts(): Alerts {
        return client.get {
            url {
                path("ServiceUpdate/InformationAlert/All")
                parameter("key", API_KEY)
            }
        }.body()
    }
}