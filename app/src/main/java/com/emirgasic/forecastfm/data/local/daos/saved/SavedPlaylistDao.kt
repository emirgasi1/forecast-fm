package com.emirgasic.forecastfm.data.local.daos.saved

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaylist(playlist: SavedPlaylistEntity)

    @Query("DELETE FROM saved_playlists WHERE playlistId = :playlistId")
    suspend fun unsavePlaylist(playlistId: String)

    @Query("SELECT * FROM saved_playlists ORDER BY savedAt DESC")
    fun getSavedPlaylists(): Flow<List<SavedPlaylistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_playlists WHERE playlistId = :playlistId)")
    fun isPlaylistSaved(playlistId: String): Flow<Boolean>
}