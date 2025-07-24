package com.jsontextfield.departurescreen.network

import com.jsontextfield.departurescreen.network.model.Alerts
import com.jsontextfield.departurescreen.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.network.model.UnionDeparturesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class DepartureScreenAPI(private val client: HttpClient) {
    suspend fun getNextService(): NextServiceResponse {
        return client.get("Stop/NextService/UN").body()
    }

    suspend fun getUnionDepartures(): UnionDeparturesResponse {
        return client.get("ServiceUpdate/UnionDepartures/All").body()
    }

    suspend fun getServiceAlerts(): Alerts {
        return client.get("ServiceUpdate/ServiceAlert/All").body()
    }

    suspend fun getInfromationAlerts(): Alerts {
        return client.get("ServiceUpdate/InformationAlert/All").body()
    }
}