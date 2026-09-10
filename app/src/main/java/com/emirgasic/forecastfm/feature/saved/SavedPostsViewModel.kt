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
    private val tokenManager: TokenManager
) : ViewModel() {

    private val savedPostRepository = SavedPostRepository()

    private val _savedPosts = MutableStateFlow<List<PostResponse>>(emptyList())
    val savedPosts: StateFlow<List<PostResponse>> = _savedPosts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSavedPosts()
    }

    fun loadSavedPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    val posts = savedPostRepository.getSavedPosts(userId)
                    _savedPosts.value = posts
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isLoading.value = false
        }
    }

    fun unsavePost(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId != null) {
                    savedPostRepository.unsavePost(postId, userId)
                    _savedPosts.value = _savedPosts.value.filter { it.id != postId }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}