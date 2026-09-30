package com.emirgasic.forecastfm.feature.music.musichistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MusicHistoryViewModel(
    private val tokenManager: TokenManager,
    private val musicHistoryRepository: MusicHistoryRepository
) : ViewModel() {

    private val _history = MutableStateFlow<List<MusicHistory>>(emptyList())
    val history: StateFlow<List<MusicHistory>> = _history.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    _history.value = musicHistoryRepository.getMusicHistory(userId)
                }
            } catch (_: Exception) {
                // Silent failure — screen shows empty state
            }
            _isLoading.value = false
        }
    }


    fun openPlaylist(playlistId: String, url: String?, onReadyToOpen: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    musicHistoryRepository.addHistory(userId, playlistId)
                    loadHistory()
                }
                if (!url.isNullOrBlank()) {
                    onReadyToOpen(url)
                }
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }
}