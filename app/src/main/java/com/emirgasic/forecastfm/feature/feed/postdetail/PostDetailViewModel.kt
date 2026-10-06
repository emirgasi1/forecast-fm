package com.emirgasic.forecastfm.feature.feed.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.post.PostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface PostDetailUiState {
    data object Loading : PostDetailUiState
    data class Success(val post: PostResponse) : PostDetailUiState
    data class Error(val message: String) : PostDetailUiState
}

class PostDetailViewModel(
    private val postApi: PostApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostDetailUiState>(PostDetailUiState.Loading)
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = PostDetailUiState.Loading
            try {
                val post = postApi.getPostById(postId)
                _uiState.value = PostDetailUiState.Success(post)
            } catch (e: Exception) {
                _uiState.value = PostDetailUiState.Error(
                    e.message ?: "Failed to load post"
                )
            }
        }
    }


    fun resolveImageUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "${ApiClient.baseUrl()}$url"
        }
    }
}