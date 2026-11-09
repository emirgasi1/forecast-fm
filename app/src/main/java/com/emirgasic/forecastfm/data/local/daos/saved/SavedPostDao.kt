package com.emirgasic.forecastfm.data.local.daos.saved

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePost(post: SavedPostEntity)

    @Query("DELETE FROM saved_posts WHERE postId = :postId")
    suspend fun unsavePost(postId: String)

    @Query("SELECT * FROM saved_posts ORDER BY savedAt DESC")
    fun getSavedPosts(): Flow<List<SavedPostEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_posts WHERE postId = :postId)")
    fun isPostSaved(postId: String): Flow<Boolean>
}