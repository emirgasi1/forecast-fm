package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.model.BusStation
import com.emirgasic.forecastfm.network.busstation.BusStationApi

class BusStationRepository(
    private val api: BusStationApi = BusStationApi()
) {

    suspend fun getAllBusStations(): List<BusStation> {
        val responses = api.getAllBusStations()
        return responses.map { response ->
            BusStation(
                id = response.id,
                name = response.name,
                latitude = response.latitude,
                longitude = response.longitude,
                lines = response.lines
            )
        }
    }
}