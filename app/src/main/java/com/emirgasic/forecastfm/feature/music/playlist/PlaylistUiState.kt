package com.emirgasic.forecastfm.feature.music.playlist

import com.emirgasic.forecastfm.data.model.Playlist

sealed interface PlaylistUiState {

    data object Loading : PlaylistUiState

    data class Success(
        val playlist: Playlist
    ) : PlaylistUiState

    data class Error(
        val message: String
    ) : PlaylistUiState
}