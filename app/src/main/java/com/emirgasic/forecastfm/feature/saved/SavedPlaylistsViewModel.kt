package com.emirgasic.forecastfm.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SavedPlaylistsViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val playlistRepository = PlaylistRepository(PlaylistApi())

    private val _savedPlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val savedPlaylists: StateFlow<List<Playlist>> = _savedPlaylists.asStateFlow()

    private val _favoritePlaylistIds = MutableStateFlow<Set<String>>(emptySet())
    val favoritePlaylistIds: StateFlow<Set<String>> = _favoritePlaylistIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSavedPlaylists()
    }

    fun loadSavedPlaylists() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val playlists = playlistRepository.getFavoritePlaylists(userId)
                    _savedPlaylists.value = playlists
                    _favoritePlaylistIds.value = playlists.map { it.id }.toSet()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
        }
    }

    fun unsavePlaylist(playlistId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    playlistRepository.unfavoritePlaylist(userId, playlistId)
                    _savedPlaylists.value = _savedPlaylists.value.filter { it.id != playlistId }
                    _favoritePlaylistIds.value = _favoritePlaylistIds.value - playlistId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    // Optionally log history
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}