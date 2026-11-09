package com.emirgasic.forecastfm.data.local.daos.music

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirgasic.forecastfm.data.local.entities.music.MusicHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicHistoryDao {
    @Query("SELECT * FROM music_history ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<MusicHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(entries: List<MusicHistoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHistoryEntry(entry: MusicHistoryEntity)

    @Query("DELETE FROM music_history")
    suspend fun clearHistory()
}