package com.emirgasic.forecastfm.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.network.weather.WeatherApi


data class WeatherRepository(
    private val weatherApi: WeatherApi = WeatherApi()
) {

    suspend fun getWeather(): WeatherData {

        val response = weatherApi.getWeather()

        val weather = Weather(
            location = response.location,
            temperature = response.temperature,
            condition = response.condition,
            feelsLike = response.feelsLike,
            humidity = response.humidity,
            wind = response.wind,
            uvIndex = response.uvIndex,
            airQuality = response.airQuality,
            icon = conditionToIcon(response.condition)
        )

        val hourly = response.hourly.map { forecast ->

            Forecast(
                time = forecast.time,
                icon = conditionToIcon(forecast.condition),
                temperature = forecast.temperature
            )
        }

        val daily = response.daily.map { forecast ->

            Forecast(
                time = forecast.time,
                icon = conditionToIcon(forecast.condition),
                temperature = forecast.temperature
            )
        }

        return WeatherData(
            weather = weather,
            hourly = hourly,
            daily = daily
        )
    }


    private fun conditionToIcon(condition: String): Int {

        return when (condition.lowercase()) {

            "sunny",
            "clear" ->
                R.drawable.sun

            "partly cloudy",
            "cloudy",
            "overcast" ->
                R.drawable.sunny_cloudy

            "rain",
            "drizzle",
            "light rain",
            "heavy rain" ->
                R.drawable.heavy_rain

            else ->
                R.drawable.sun
        }
    }
}

data class WeatherData(
    val weather: Weather,
    val hourly: List<Forecast>,
    val daily: List<Forecast>
)