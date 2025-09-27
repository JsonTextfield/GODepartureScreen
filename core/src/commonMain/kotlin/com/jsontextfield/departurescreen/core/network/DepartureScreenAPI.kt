package com.jsontextfield.departurescreen.core.network

import com.jsontextfield.departurescreen.core.network.model.Alerts
import com.jsontextfield.departurescreen.core.network.model.ExceptionsResponse
import com.jsontextfield.departurescreen.core.network.model.NextServiceResponse
import com.jsontextfield.departurescreen.core.network.model.StopResponse
import com.jsontextfield.departurescreen.core.network.model.UnionDeparturesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
class DepartureScreenAPI(private val client: HttpClient) {

    suspend fun getAllStops(): StopResponse {
        return client.get("Stop/All").body()
    }
    suspend fun getNextService(stationCode: String): NextServiceResponse {
        return client.get("Stop/NextService/$stationCode").body()
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

    suspend fun getExceptions(): ExceptionsResponse {
        return client.get("ServiceUpdate/Exceptions/Train").body()
    }
}