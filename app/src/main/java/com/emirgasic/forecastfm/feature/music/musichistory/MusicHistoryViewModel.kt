package com.emirgasic.forecastfm.feature.music.musichistory

import android.content.Context
import android.content.Intent
import android.net.Uri
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
    private val tokenManager: TokenManager
) : ViewModel() {

    private val repository = MusicHistoryRepository()

    private val _history = MutableStateFlow<List<MusicHistory>>(emptyList())
    val history: StateFlow<List<MusicHistory>> = _history.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val historyData = repository.getMusicHistory(userId)
                    _history.value = historyData
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
        }
    }

    fun openPlaylist(playlistId: String, url: String?, context: Context) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    repository.addHistory(userId, playlistId)
                    loadHistory() // Refresh history
                }
                if (!url.isNullOrBlank()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}