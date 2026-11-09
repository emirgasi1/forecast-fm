package com.emirgasic.forecastfm.feature.music.musichistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicHistoryViewModel(
    private val tokenManager: TokenManager,
    private val musicHistoryRepository: MusicHistoryRepository
) : ViewModel() {

    // 1. Observe Room directly
    val history: StateFlow<List<MusicHistory>> = musicHistoryRepository.getLocalHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    // Sync to Room in the background
                    musicHistoryRepository.syncHistoryFromApi(userId)
                }
            } catch (_: Exception) {
                // Silent failure
            }
            _isLoading.value = false
        }
    }

    fun openPlaylist(playlistId: String, url: String?, onReadyToOpen: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    // Add history to backend, then sync it back to Room
                    musicHistoryRepository.addHistory(userId, playlistId)
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