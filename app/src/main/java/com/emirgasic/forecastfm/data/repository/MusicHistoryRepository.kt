package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.network.musichistory.MusicHistoryApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class MusicHistoryRepository(
    private val musicHistoryApi: MusicHistoryApi = MusicHistoryApi()
) {

    suspend fun getMusicHistory(
        userId: String
    ): List<MusicHistory> {

        val response =
            musicHistoryApi.getHistory(userId)

        return response.map { history ->

            val instant =
                Instant.parse(history.playedAt)

            val localDateTime =
                instant.atZone(
                    ZoneId.systemDefault()
                ).toLocalDateTime()

            MusicHistory(
                id = history.id,
                section = getSection(localDateTime.toLocalDate()) ,
                title = history.title,
                weather = history.weather,
                temperature = history.temperature,
                location = history.location,
                time = formatTime(localDateTime),
                weatherIcon = conditionToIcon(history.weather)
            )
        }
    }

    private fun getSection(
        date: LocalDate
    ): String {

        val today = LocalDate.now()

        val daysAgo =
            ChronoUnit.DAYS.between(
                date,
                today
            )

        return when {

            daysAgo == 0L ->
                "Today"

            daysAgo == 1L ->
                "Yesterday"

            daysAgo == 2L ->
                "2 Days Ago"

            daysAgo in 3L..7L ->
                "Last Week"

            else ->
                "Older"
        }
    }

    private fun formatTime(
        dateTime: java.time.LocalDateTime
    ): String{

        val hour =
            if (dateTime.hour % 12 == 0) {
                12
            } else {
                dateTime.hour % 12
            }

        val minute =
            dateTime.minute
                .toString()
                .padStart(2, '0')

        val period =
            if (dateTime.hour < 12) {
                "AM"
            } else {
                "PM"
            }

        return "$hour:$minute $period"
    }

    private fun conditionToIcon(
        weather: String
    ): Int {

        return when (weather.lowercase()) {

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

    suspend fun addHistory(
        userId: String,
        playlistId: String
    ) {
        musicHistoryApi.addHistory(
            userId = userId,
            playlistId = playlistId
        )
    }
}