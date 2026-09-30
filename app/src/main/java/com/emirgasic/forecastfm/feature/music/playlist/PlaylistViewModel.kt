package com.emirgasic.forecastfm.feature.music.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.utils.YouTubeMapper
import com.emirgasic.forecastfm.core.utils.YouTubeUtils
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.youtube.YouTubeApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val tokenManager: TokenManager,
    private val playlistRepository: PlaylistRepository,
    private val youTubeApi: YouTubeApi
) : ViewModel() {

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

                if (youtubePlaylistId != null && youtubePlaylistId.isNotBlank()) {
                    val items = try {
                        youTubeApi.getPlaylistItems(youtubePlaylistId)
                    } catch (_: Exception) {
                        emptyList()
                    }

                    if (items.isNotEmpty()) {
                        songs = YouTubeMapper.playlistItemsToMusic(items)

                        if (imageUrl.isNullOrBlank()) {
                            val thumbnail = items.firstOrNull()?.thumbnailUrl

                            if (!thumbnail.isNullOrBlank()) {
                                imageUrl = thumbnail
                                try {
                                    playlistRepository.updatePlaylistImage(playlistId, thumbnail)
                                } catch (_: Exception) {
                                    // Non-critical — image persistence failed
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

                // Load initial favorite state
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val favorites = try {
                        playlistRepository.getFavoritePlaylistIds(userId)
                    } catch (_: Exception) {
                        emptyList()
                    }
                    _isFavorite.value = playlistId in favorites
                }

                loadSimilarPlaylists(playlist)

            } catch (e: Exception) {
                _uiState.value = PlaylistUiState.Error(
                    e.message ?: "Failed to load playlist"
                )
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
        } catch (_: Exception) {
        }
    }

    fun toggleFavorite(playlistId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch

                val currentlyFavorite = _isFavorite.value

                if (currentlyFavorite) {
                    playlistRepository.unfavoritePlaylist(userId, playlistId)
                } else {
                    playlistRepository.favoritePlaylist(userId, playlistId)
                }

                _isFavorite.value = !currentlyFavorite
            } catch (_: Exception) {
                // Silent failure — isFavorite state unchanged
            }
        }
    }
}