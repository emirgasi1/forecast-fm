package com.emirgasic.forecastfm.feature.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.SavedPostRepository
import com.emirgasic.forecastfm.network.post.PostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SavedPostsViewModel(
    private val tokenManager: TokenManager,
    private val savedPostRepository: SavedPostRepository
) : ViewModel() {

    private val _savedPosts = MutableStateFlow<List<PostResponse>>(emptyList())
    val savedPosts: StateFlow<List<PostResponse>> = _savedPosts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSavedPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    _savedPosts.value = savedPostRepository.getSavedPosts(userId)
                }
            } catch (_: Exception) {
                // Silent failure
            }
            _isLoading.value = false
        }
    }

    fun unsavePost(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch

                savedPostRepository.unsavePost(postId, userId)
                _savedPosts.value = _savedPosts.value.filter { it.id != postId }
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }
}