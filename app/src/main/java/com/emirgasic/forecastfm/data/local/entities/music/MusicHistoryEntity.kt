package com.emirgasic.forecastfm.data.local.entities.music

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_history")
data class MusicHistoryEntity(
    @PrimaryKey val id: String,
    val section: String,
    val title: String,
    val weather: String,
    val temperature: String,
    val location: String,
    val time: String,
    val weatherCondition: String, // 👈 Store the string "sunny", "rainy" instead of the Int
    val timestamp: Long = System.currentTimeMillis() // For sorting
)