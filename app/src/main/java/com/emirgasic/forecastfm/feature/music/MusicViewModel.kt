
package com.emirgasic.forecastfm.feature.music

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class MusicViewModel : ViewModel() {

    private val repository = PlaylistRepository()


    private val _playlists = MutableStateFlow(
        repository.getPlaylists()
    )

    val playlists = _playlists.asStateFlow()


    val weatherPlaylists = MutableStateFlow(
        repository.getPlaylists().take(2)
    )


    val trendingPlaylists = MutableStateFlow(
        repository.getPlaylists().drop(1)
    )

}