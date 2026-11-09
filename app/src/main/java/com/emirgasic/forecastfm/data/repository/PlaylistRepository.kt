package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.local.daos.music.PlaylistDao
import com.emirgasic.forecastfm.data.local.entities.music.SongEntity
import com.emirgasic.forecastfm.data.local.entities.playlist.PlaylistEntity
import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepository(
    private val playlistDao: PlaylistDao, // 👈 Add Room DAO
    private val playlistApi: PlaylistApi
) {

    // ==========================================
    // LOCAL / ROOM FUNCTIONS
    // ==========================================

    fun getLocalPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toDomain(emptyList()) } // We'll map songs separately if needed
        }
    }

    fun getLocalPlaylistById(playlistId: String): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId).map { entity ->
            entity?.toDomain(emptyList())
        }
    }

    suspend fun syncPlaylistsFromApi() {
        try {
            val responses = playlistApi.getPlaylists()
            val playlistEntities = mutableListOf<PlaylistEntity>()
            val songEntities = mutableListOf<SongEntity>()

            responses.forEach { response ->
                playlistEntities.add(
                    PlaylistEntity(
                        id = response.id,
                        title = response.title,
                        genre = response.genre,
                        mood = response.mood,
                        albumImageUrl = response.albumImageUrl,
                        weather = response.weather,
                        temperature = response.temperature,
                        location = response.location,
                        likes = response.likes,
                        spotifyUrl = response.spotifyUrl,
                        youtubeUrl = response.youtubeUrl,
                        bestFor = response.bestFor.joinToString(",") // 👈 Join list to String
                    )
                )

                response.songs.forEach { song ->
                    songEntities.add(
                        SongEntity(
                            id = song.id,
                            playlistId = response.id,
                            title = song.title,
                            artist = song.artist,
                            duration = song.duration,
                            albumImageUrl = song.albumImageUrl
                        )
                    )
                }
            }

            playlistDao.syncPlaylists(playlistEntities, songEntities)
        } catch (e: Exception) {
            // Offline: keep old Room data
        }
    }

    // ==========================================
    // NETWORK FUNCTIONS (Kept for compatibility)
    // ==========================================

    suspend fun getFavoritePlaylists(userId: String): List<Playlist> = withContext(Dispatchers.IO) {
        playlistApi.getSavedPlaylists(userId).map { it.toPlaylist() }
    }

    suspend fun getPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        playlistApi.getPlaylists().take(10).map { it.toPlaylist() }
    }

    suspend fun getPlaylist(id: String): Playlist = withContext(Dispatchers.IO) {
        playlistApi.getPlaylist(id).toPlaylist()
    }

    suspend fun favoritePlaylist(userId: String, playlistId: String) = withContext(Dispatchers.IO) {
        playlistApi.favoritePlaylist(userId = userId, playlistId = playlistId)
    }

    suspend fun unfavoritePlaylist(userId: String, playlistId: String) = withContext(Dispatchers.IO) {
        playlistApi.unfavoritePlaylist(userId = userId, playlistId = playlistId)
    }

    suspend fun getFavoritePlaylistIds(userId: String): List<String> = withContext(Dispatchers.IO) {
        playlistApi.getFavoritePlaylistIds(userId = userId)
    }

    suspend fun getRecommendedPlaylist(location: String, weather: String): Playlist? = withContext(Dispatchers.IO) {
        val playlists = getPlaylists()
        playlists.firstOrNull {
            it.location.equals(location, ignoreCase = true) && it.weather.equals(weather, ignoreCase = true)
        } ?: playlists.firstOrNull {
            it.location.equals(location, ignoreCase = true)
        }
    }

    suspend fun updatePlaylistImage(playlistId: String, imageUrl: String) = withContext(Dispatchers.IO) {
        playlistApi.updatePlaylistImage(playlistId, imageUrl)
    }


    private fun PlaylistEntity.toDomain(songs: List<Music>) = Playlist(
        id = id,
        title = title,
        genre = genre,
        mood = mood,
        albumImageUrl = albumImageUrl,
        weather = weather,
        temperature = temperature,
        location = location,
        songs = songs,
        likes = likes,
        spotifyUrl = spotifyUrl,
        youtubeUrl = youtubeUrl,
        bestFor = bestFor.split(",").filter { it.isNotBlank() }
    )

    private fun com.emirgasic.forecastfm.network.playlist.PlaylistResponse.toPlaylist() = Playlist(
        id = id, title = title, genre = genre, mood = mood, albumImageUrl = albumImageUrl,
        weather = weather, temperature = temperature, location = location,
        songs = songs.map { song ->
            Music(id = song.id, title = song.title, artist = song.artist, duration = song.duration, albumImageUrl = song.albumImageUrl)
        },
        likes = likes, spotifyUrl = spotifyUrl, youtubeUrl = youtubeUrl, bestFor = bestFor
    )
}