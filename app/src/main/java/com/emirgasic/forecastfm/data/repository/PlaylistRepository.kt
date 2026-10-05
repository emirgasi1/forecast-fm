package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepository(
    private val playlistApi: PlaylistApi
) {

    suspend fun getFavoritePlaylists(userId: String): List<Playlist> = withContext(Dispatchers.IO) {
        playlistApi.getSavedPlaylists(userId).map { it.toPlaylist() }
    }

    suspend fun getPlaylists(): List<Playlist> = withContext(Dispatchers.IO) {
        playlistApi.getPlaylists()
            .take(10)
            .map { it.toPlaylist() }
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

    suspend fun getRecommendedPlaylist(location: String, weather: String): Playlist? =
        withContext(Dispatchers.IO) {
            val playlists = getPlaylists()
            playlists.firstOrNull {
                it.location.equals(location, ignoreCase = true) &&
                        it.weather.equals(weather, ignoreCase = true)
            } ?: playlists.firstOrNull {
                it.location.equals(location, ignoreCase = true)
            }
        }

    suspend fun updatePlaylistImage(playlistId: String, imageUrl: String) =
        withContext(Dispatchers.IO) {
            playlistApi.updatePlaylistImage(playlistId, imageUrl)
        }

    private fun com.emirgasic.forecastfm.network.playlist.PlaylistResponse.toPlaylist() =
        Playlist(
            id = id,
            title = title,
            genre = genre,
            mood = mood,
            albumImageUrl = albumImageUrl,
            weather = weather,
            temperature = temperature,
            location = location,
            songs = songs.map { song ->
                Music(
                    id = song.id,
                    title = song.title,
                    artist = song.artist,
                    duration = song.duration,
                    albumImageUrl = song.albumImageUrl
                )
            },
            likes = likes,
            spotifyUrl = spotifyUrl,
            youtubeUrl = youtubeUrl,
            bestFor = bestFor
        )
}