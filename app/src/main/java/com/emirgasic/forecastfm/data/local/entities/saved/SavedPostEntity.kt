package com.emirgasic.forecastfm.data.local.entities.saved

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_posts")
data class SavedPostEntity(
    @PrimaryKey val postId: String,
    val savedAt: Long = System.currentTimeMillis()
)