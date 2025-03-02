package com.jsontextfield.departurescreen.network

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
                path("api/V1/Stop/NextService/UN")
                parameter("key", API_KEY)
            }
        }.body()
    }

    suspend fun getUnionDepartures(): UnionDeparturesResponse {
        return client.get {
            url {
                path("api/V1/ServiceUpdate/UnionDepartures/All")
                parameter("key", API_KEY)
            }
        }.body()
    }
}