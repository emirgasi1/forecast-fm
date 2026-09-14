package com.emirgasic.forecastfm.network.busstation

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class BusStationApi {

    suspend fun getAllBusStations(): List<BusStationResponse> {
        return ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/bus-stations"
        ).body()
    }
}