package com.emirgasic.forecastfm.feature.music.playlist

import androidx.lifecycle.ViewModel
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class PlaylistViewModel : ViewModel() {


    private val repository = PlaylistRepository()


    private val _playlist =
        MutableStateFlow<Playlist?>(null)

    val playlist: StateFlow<Playlist?> =
        _playlist.asStateFlow()



    fun loadPlaylist(id:String){

        val playlist =
            repository.getPlaylists()
                .find {
                    it.id == id
                }

        _playlist.value = playlist
    }

}