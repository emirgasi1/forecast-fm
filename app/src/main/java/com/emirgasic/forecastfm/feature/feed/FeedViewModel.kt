package com.emirgasic.forecastfm.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.FeedPost
import com.emirgasic.forecastfm.data.repository.FeedRepository
import com.emirgasic.forecastfm.data.repository.PostRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.user.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class FeedViewModel : ViewModel() {

    private val userApi = UserApi()
    private val userRepository = UserRepository(userApi)
    private val postApi= PostApi()
    private val postRepository= PostRepository(postApi)

    private val repository=
        FeedRepository(
            userRepository=userRepository,
            postRepository=postRepository
        )


    private val _uiState =
        MutableStateFlow<FeedUiState>(FeedUiState.Loading)

    val uiState: StateFlow<FeedUiState> =
        _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {

        viewModelScope.launch {

            _uiState.value = FeedUiState.Loading

            try {

                val posts = repository.getPosts()

                _uiState.value =
                    FeedUiState.Success(posts)

            } catch (e: Exception) {

                _uiState.value =
                    FeedUiState.Error(
                        e.message ?: "Failed to load feed"
                    )
            }
        }
    }
}