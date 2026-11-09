package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.local.daos.saved.SavedPlaylistDao
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPlaylistEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedPlaylistRepository(
    private val savedPlaylistDao: SavedPlaylistDao
) {
    fun getSavedPlaylistIds(): Flow<List<String>> {
        return savedPlaylistDao.getSavedPlaylists().map { list -> list.map { it.playlistId } }
    }

    fun isPlaylistSaved(playlistId: String): Flow<Boolean> {
        return savedPlaylistDao.isPlaylistSaved(playlistId)
    }

    suspend fun toggleSavePlaylist(playlistId: String, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            savedPlaylistDao.unsavePlaylist(playlistId)
        } else {
            savedPlaylistDao.savePlaylist(SavedPlaylistEntity(playlistId))
        }
    }
}