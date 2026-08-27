package com.emirgasic.forecastfm.network.weather

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get


class WeatherApi {

    suspend fun getWeather(): WeatherResponse {

        return ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/weather"
        ).body()
    }
}