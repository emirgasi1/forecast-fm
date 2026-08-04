package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Weather

class WeatherRepository {

    fun getCurrentWeather(): Weather {
        return Weather(
            location = "Baščaršija",
            temperature = "22°C",
            condition = "Sunny",
            feelsLike = "24°C",
            humidity = "55%",
            wind = "8 km/h",
            uvIndex = "20UV",
            airQuality = "20"
        )
    }

    fun getHourlyForecast(): List<Forecast> {
        return listOf(
            Forecast("09:00", R.drawable.sun, "18°C"),
            Forecast("12:00", R.drawable.sunny_cloudy, "22°C"),
            Forecast("15:00", R.drawable.sun, "24°C"),
            Forecast("18:00", R.drawable.sunny_cloudy, "21°C"),
            Forecast("21:00", R.drawable.heavy_rain, "16°C")
        )
    }

    fun getDailyForecast(): List<Forecast> {
        return listOf(
            Forecast("Mon", R.drawable.sun, "22°C"),
            Forecast("Tue", R.drawable.sunny_cloudy, "20°C"),
            Forecast("Wed", R.drawable.heavy_rain, "17°C"),
            Forecast("Thu", R.drawable.sun, "24°C"),
            Forecast("Fri", R.drawable.sunny_cloudy, "19°C")
        )
    }
}