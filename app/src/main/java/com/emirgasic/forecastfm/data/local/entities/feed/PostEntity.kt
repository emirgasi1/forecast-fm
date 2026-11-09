package com.emirgasic.forecastfm.data.local.entities.feed

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_feed_posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val jsonData: String,
    val cachedAt: Long = System.currentTimeMillis()
)