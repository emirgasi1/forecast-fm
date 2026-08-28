package com.emirgasic.forecastfm.feature.music.musichistory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.user.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri

class MusicHistoryViewModel : ViewModel() {

    private val repository =
        MusicHistoryRepository()

    private val userRepository =
        UserRepository(
            userApi = UserApi()
        )
    private val musicHistoryRepository =
        MusicHistoryRepository()
    private val _history =
        MutableStateFlow<List<MusicHistory>>(emptyList())

    val history: StateFlow<List<MusicHistory>> =
        _history.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {

        viewModelScope.launch {

            try {

                val user =
                    userRepository.getCurrentUser()

                if (user == null) {
                    return@launch
                }

                _history.value =
                    repository.getMusicHistory(
                        userId = user.id
                    )

            } catch (e: Exception) {

                e.printStackTrace()

                println(
                    "MUSIC HISTORY ERROR: ${e.message}"
                )
            }
        }
    }

    fun openPlaylist(
        playlistId: String,
        url: String?,
        context: Context
    ) {

        viewModelScope.launch {

            try {

                val user =
                    userRepository.getCurrentUser()

                musicHistoryRepository.addHistory(
                    userId = user.id,
                    playlistId = playlistId
                )

                if (!url.isNullOrBlank()) {

                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(url)
                    )

                    context.startActivity(intent)
                }

            } catch (e: Exception) {

                println(
                    "OPEN PLAYLIST ERROR: ${e.message}"
                )
            }
        }
    }
}