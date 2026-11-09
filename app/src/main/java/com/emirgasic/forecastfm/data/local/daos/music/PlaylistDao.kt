package com.emirgasic.forecastfm.data.local.daos.music

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.emirgasic.forecastfm.data.local.entities.music.SongEntity
import com.emirgasic.forecastfm.data.local.entities.playlist.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistById(playlistId: String): Flow<PlaylistEntity?>

    @Query("SELECT * FROM songs WHERE playlistId = :playlistId")
    fun getSongsForPlaylist(playlistId: String): Flow<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("DELETE FROM playlists")
    suspend fun clearPlaylists()

    @Query("DELETE FROM songs")
    suspend fun clearSongs()

    @Transaction
    suspend fun syncPlaylists(playlists: List<PlaylistEntity>, songs: List<SongEntity>) {
        clearPlaylists()
        clearSongs()
        insertPlaylists(playlists)
        insertSongs(songs)
    }
}