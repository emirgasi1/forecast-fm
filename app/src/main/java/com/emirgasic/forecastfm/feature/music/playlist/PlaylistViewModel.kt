package com.emirgasic.forecastfm.feature.music.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistViewModel : ViewModel() {

    private val playlistRepository = PlaylistRepository(PlaylistApi())

    private val _uiState = MutableStateFlow<PlaylistUiState>(PlaylistUiState.Loading)
    val uiState: StateFlow<PlaylistUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun loadPlaylist(playlistId: String) {
        viewModelScope.launch {
            _uiState.value = PlaylistUiState.Loading
            try {
                val playlist = playlistRepository.getPlaylist(playlistId)
                _uiState.value = PlaylistUiState.Success(playlist)
            } catch (e: Exception) {
                _uiState.value = PlaylistUiState.Error(e.message ?: "Failed to load playlist")
            }
        }
    }

    fun toggleFavorite(playlistId: String) {
        _isFavorite.value = !_isFavorite.value
    }
}