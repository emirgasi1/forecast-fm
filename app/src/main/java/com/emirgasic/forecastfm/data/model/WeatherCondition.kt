package com.emirgasic.forecastfm.data.model

import androidx.annotation.DrawableRes
import com.emirgasic.forecastfm.R

enum class WeatherCondition(
    val displayName: String,
    @DrawableRes val iconRes: Int
) {
    SUNNY("Sunny", R.drawable.sun),
    PARTLY_CLOUDY("Partly Cloudy", R.drawable.sunny_cloudy),
    CLOUDY("Cloudy", R.drawable.sunny_cloudy),
    FOGGY("Foggy", R.drawable.sunny_cloudy),
    DRIZZLE("Drizzle", R.drawable.heavy_rain),
    RAIN("Rain", R.drawable.heavy_rain),
    SNOW("Snow", R.drawable.heavy_rain),
    THUNDERSTORM("Thunderstorm", R.drawable.heavy_rain),
    UNKNOWN("Weather", R.drawable.sun);

    val bucket: Bucket
        get() = when (this) {
            SUNNY -> Bucket.SUNNY
            PARTLY_CLOUDY, CLOUDY, FOGGY -> Bucket.CLOUDY
            DRIZZLE, RAIN, THUNDERSTORM -> Bucket.RAIN
            SNOW -> Bucket.SNOW
            UNKNOWN -> Bucket.ALL
        }

    enum class Bucket { SUNNY, CLOUDY, RAIN, SNOW, ALL }

    companion object {
        fun from(raw: String?): WeatherCondition =
            when (raw?.trim()?.lowercase()) {
                "sunny", "clear" -> SUNNY
                "partly cloudy" -> PARTLY_CLOUDY
                "cloudy", "overcast" -> CLOUDY
                "foggy", "fog", "mist" -> FOGGY
                "drizzle", "light rain" -> DRIZZLE
                "rain", "heavy rain", "showers" -> RAIN
                "snow", "light snow", "heavy snow" -> SNOW
                "thunderstorm", "storm" -> THUNDERSTORM
                else -> UNKNOWN
            }
    }
}