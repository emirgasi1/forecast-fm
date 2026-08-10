package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Home
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.model.Weather

class HomeRepository {

    fun getHome(): Home {

        val weather = Weather(
            location = "Baščaršija",
            temperature = "12°C",
            condition = "Sunny",
            feelsLike = "Thunderous",
            humidity = "Strong",
            wind = "20 km/h",
            uvIndex = "3",
            airQuality = "Good"
        )


        val forecast = listOf(
            Forecast(
                time = "Mon",
                icon = R.drawable.sun,
                temperature = "12°C"
            ),
            Forecast(
                time = "Tue",
                icon = R.drawable.sunny_cloudy,
                temperature = "2°C"
            ),
            Forecast(
                time = "Wed",
                icon = R.drawable.sun,
                temperature = "22°C"
            ),
            Forecast(
                time = "Thu",
                icon = R.drawable.sunny_cloudy,
                temperature = "10°C"
            ),
            Forecast(
                time = "Fri",
                icon = R.drawable.heavy_rain,
                temperature = "-2°C"
            )
        )


        val playlists = listOf(

            Playlist(
                id = "1",
                title = "Midnight Coffee",
                genre = "Lo-fi",
                mood = "Relaxed",
                albumImage = R.drawable.fire,
                weather = "Sunny",
                temperature = "12°C",
                location = "Baščaršija",
                songs = emptyList(),
                likes = 54
            ),

            Playlist(
                id = "2",
                title = "Downtown Coffee",
                genre = "Jazz",
                mood = "Chill",
                albumImage = R.drawable.like,
                weather = "Sunny",
                temperature = "12°C",
                location = "Baščaršija",
                songs = emptyList(),
                likes = 10
            ),

            Playlist(
                id = "3",
                title = "Morning Coffee",
                genre = "Rock",
                mood = "Energetic",
                albumImage = R.drawable.okay,
                weather = "Sunny",
                temperature = "12°C",
                location = "Baščaršija",
                songs = emptyList(),
                likes = 302
            )

        )


        return Home(
            greeting = "Good Morning",
            weather = weather,
            forecast = forecast,
            playlists = playlists
        )

    }

}