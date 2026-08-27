package com.emirgasic.forecastfm.feature.music.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.network.user.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PlaylistViewModel : ViewModel() {

    private val playlistRepository = PlaylistRepository(
        playlistApi = PlaylistApi()
    )

    private val userRepository = UserRepository(
        userApi = UserApi()
    )

    private var currentUserId: String? = null

    private val _uiState =
        MutableStateFlow<PlaylistUiState>(PlaylistUiState.Loading)

    val uiState: StateFlow<PlaylistUiState> =
        _uiState.asStateFlow()

    private val _isFavorite =
        MutableStateFlow(false)

    val isFavorite: StateFlow<Boolean> =
        _isFavorite.asStateFlow()


    fun loadPlaylist(id: String) {

        viewModelScope.launch {

            _uiState.value = PlaylistUiState.Loading

            try {

                // Get the current user
                val user = userRepository.getCurrentUser()

                currentUserId = user.id

                // Get the playlist
                val playlist =
                    playlistRepository.getPlaylists()
                        .find { it.id == id }

                if (playlist == null) {

                    _uiState.value =
                        PlaylistUiState.Error(
                            "Playlist not found"
                        )

                    return@launch
                }

                // Get user's favorite playlists
                val favoriteIds =
                    playlistRepository.getFavoritePlaylistIds(
                        userId = user.id
                    )

                _isFavorite.value =
                    playlist.id in favoriteIds

                _uiState.value =
                    PlaylistUiState.Success(playlist)

            } catch (e: Exception) {

                e.printStackTrace()

                _uiState.value =
                    PlaylistUiState.Error(
                        "Unable to load playlist"
                    )
            }
        }
    }


    fun toggleFavorite(playlistId: String) {

        viewModelScope.launch {

            val userId = currentUserId
                ?: return@launch

            try {

                if (_isFavorite.value) {

                    playlistRepository.unfavoritePlaylist(
                        userId = userId,
                        playlistId = playlistId
                    )

                    _isFavorite.value = false

                } else {

                    playlistRepository.favoritePlaylist(
                        userId = userId,
                        playlistId = playlistId
                    )

                    _isFavorite.value = true
                }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }
}