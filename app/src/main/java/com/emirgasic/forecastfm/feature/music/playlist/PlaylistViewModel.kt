package com.emirgasic.forecastfm.feature.music.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.utils.YouTubeMapper
import com.emirgasic.forecastfm.core.utils.YouTubeUtils
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.network.youtube.YouTubeApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {

    private val playlistApi = PlaylistApi()
    private val playlistRepository = PlaylistRepository(playlistApi)
    private val youTubeApi = YouTubeApi()

    private val _uiState = MutableStateFlow<PlaylistUiState>(PlaylistUiState.Loading)
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _similarPlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val similarPlaylists: StateFlow<List<Playlist>> = _similarPlaylists.asStateFlow()

    fun loadPlaylist(playlistId: String) {
        viewModelScope.launch {
            _uiState.value = PlaylistUiState.Loading
            try {
                val playlist = playlistRepository.getPlaylist(playlistId)

                var imageUrl = playlist.albumImageUrl
                var songs = playlist.songs

                val youtubePlaylistId = YouTubeUtils.extractPlaylistId(playlist.youtubeUrl)

                if (youtubePlaylistId != null) {
                    val items = try {
                        youTubeApi.getPlaylistItems(youtubePlaylistId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emptyList()
                    }

                    if (items.isNotEmpty()) {
                        songs = YouTubeMapper.playlistItemsToMusic(items)

                        if (imageUrl.isNullOrBlank()) {
                            val thumbnail = items.firstOrNull()?.thumbnailUrl

                            if (!thumbnail.isNullOrBlank()) {
                                imageUrl = thumbnail
                                try {
                                    playlistApi.updatePlaylistImage(playlistId, thumbnail)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }

                val updatedPlaylist = playlist.copy(
                    songs = songs,
                    albumImageUrl = imageUrl
                )

                _uiState.value = PlaylistUiState.Success(updatedPlaylist)

                loadSimilarPlaylists(playlist)

            } catch (e: Exception) {
                _uiState.value = PlaylistUiState.Error(e.message ?: "Failed to load playlist")
            }
        }
    }

    private suspend fun loadSimilarPlaylists(currentPlaylist: Playlist) {
        try {
            val allPlaylists = playlistRepository.getPlaylists()

            val similar = allPlaylists
                .filter { it.id != currentPlaylist.id }
                .map { playlist ->
                    var score = 0
                    if (playlist.genre.equals(currentPlaylist.genre, ignoreCase = true)) score++
                    if (playlist.mood.equals(currentPlaylist.mood, ignoreCase = true)) score++
                    if (playlist.weather.equals(currentPlaylist.weather, ignoreCase = true)) score++
                    if (playlist.location.equals(currentPlaylist.location, ignoreCase = true)) score++
                    Pair(playlist, score)
                }
                .sortedByDescending { it.second }
                .take(3)
                .map { it.first }

            _similarPlaylists.value = similar
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleFavorite(playlistId: String) {
        _isFavorite.value = !_isFavorite.value
    }
}