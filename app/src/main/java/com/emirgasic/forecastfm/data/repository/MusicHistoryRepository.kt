package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.local.daos.music.MusicHistoryDao
import com.emirgasic.forecastfm.data.local.entities.music.MusicHistoryEntity
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.network.musichistory.MusicHistoryApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class MusicHistoryRepository(
    private val historyDao: MusicHistoryDao, // 👈 Room dependency
    private val musicHistoryApi: MusicHistoryApi = MusicHistoryApi() // 👈 Network dependency
) {

    // 1. READ FROM ROOM (Instant UI updates)
    fun getLocalHistory(): Flow<List<MusicHistory>> {
        return historyDao.getHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // 2. SYNC FROM API TO ROOM (Background)
    suspend fun syncHistoryFromApi(userId: String) {
        try {
            val response = musicHistoryApi.getHistory(userId)
            val entities = response.map { history ->
                MusicHistoryEntity(
                    id = history.id,
                    section = getSection(Instant.parse(history.playedAt).atZone(ZoneId.systemDefault()).toLocalDate()),
                    title = history.title,
                    weather = history.weather,
                    temperature = history.temperature,
                    location = history.location,
                    time = formatTime(Instant.parse(history.playedAt).atZone(ZoneId.systemDefault()).toLocalDateTime()),
                    weatherCondition = history.weather,
                    timestamp = Instant.parse(history.playedAt).toEpochMilli()
                )
            }
            historyDao.clearHistory()
            historyDao.insertHistory(entities)
        } catch (e: Exception) {
            // Offline - keep the existing Room data
        }
    }

    // 3. ADD HISTORY ENTRY (Call when user opens a playlist)
    suspend fun addHistory(userId: String, playlistId: String) {
        try {
            // Send to backend
            musicHistoryApi.addHistory(userId, playlistId)
            // Sync back to Room to get the new data
            syncHistoryFromApi(userId)
        } catch (e: Exception) {
            // If offline, we skip the network and just let the UI stay as is
        }
    }

    // --- Mappers and Helpers ---

    private fun MusicHistoryEntity.toDomain() = MusicHistory(
        id = id,
        section = section,
        title = title,
        weather = weather,
        temperature = temperature,
        location = location,
        time = time,
        weatherIcon = conditionToIcon(weatherCondition)
    )

    private fun getSection(date: LocalDate): String {
        val today = LocalDate.now()
        val daysAgo = ChronoUnit.DAYS.between(date, today)
        return when {
            daysAgo == 0L -> "Today"
            daysAgo == 1L -> "Yesterday"
            daysAgo == 2L -> "2 Days Ago"
            daysAgo in 3L..7L -> "Last Week"
            else -> "Older"
        }
    }

    private fun formatTime(dateTime: java.time.LocalDateTime): String {
        val hour = if (dateTime.hour % 12 == 0) 12 else dateTime.hour % 12
        val minute = dateTime.minute.toString().padStart(2, '0')
        val period = if (dateTime.hour < 12) "AM" else "PM"
        return "$hour:$minute $period"
    }

    private fun conditionToIcon(weather: String): Int {
        return when (weather.lowercase()) {
            "sunny", "clear" -> R.drawable.sun
            "partly cloudy", "cloudy", "overcast" -> R.drawable.sunny_cloudy
            "rain", "drizzle", "light rain", "heavy rain" -> R.drawable.heavy_rain
            else -> R.drawable.sun
        }
    }
}