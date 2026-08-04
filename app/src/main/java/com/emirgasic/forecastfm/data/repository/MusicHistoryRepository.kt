package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.MusicHistory

class MusicHistoryRepository {

    private val history = listOf(

        MusicHistory(
            id = "1",
            section = "Today",
            title = "Morning Coffee Jazz",
            weatherIcon = R.drawable.sun,
            weather = "Sunny",
            temperature = "12°C",
            location = "Baščaršija",
            time = "08:34 AM"
        ),

        MusicHistory(
            id = "2",
            section = "Yesterday",
            title = "Rainy Evening Lo-fi",
            weatherIcon = R.drawable.heavy_rain,
            weather = "Rainy",
            temperature = "8°C",
            location = "Ilidža",
            time = "07:15 PM"
        ),

        MusicHistory(
            id = "3",
            section = "2 Days Ago",
            title = "Night Drive",
            weatherIcon = R.drawable.sunny_cloudy,
            weather = "Cloudy",
            temperature = "15°C",
            location = "Otoka",
            time = "10:42 PM"
        ),

        MusicHistory(
            id = "4",
            section = "Last Week",
            title = "Study Session",
            weatherIcon = R.drawable.sun,
            weather = "Sunny",
            temperature = "24°C",
            location = "Dobrinja",
            time = "09:10 AM"
        )
    )

    fun getMusicHistory(): List<MusicHistory> {
        return history
    }
}