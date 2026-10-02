package com.emirgasic.forecastfm.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.FeedRepository
import com.emirgasic.forecastfm.data.repository.LikeRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedPostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FeedViewModel(
    private val tokenManager: TokenManager,
    private val feedRepository: FeedRepository,
    private val likeRepository: LikeRepository,
    private val savedPostRepository: SavedPostRepository,
    private val savedOutfitRepository: SavedOutfitRepository,
    private val outfitRepository: OutfitRepository,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    private val _likedPosts = MutableStateFlow<Set<String>>(emptySet())
    private val _savedPosts = MutableStateFlow<Set<String>>(emptySet())
    val savedPosts: StateFlow<Set<String>> = _savedPosts.asStateFlow()
    val likedPosts: StateFlow<Set<String>> = _likedPosts.asStateFlow()

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading

            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val posts = feedRepository.getPosts(userId)

                val likedSet = mutableSetOf<String>()
                posts.forEach { post ->
                    try {
                        if (likeRepository.isPostLiked(post.id, userId)) {
                            likedSet.add(post.id)
                        }
                    } catch (_: Exception) {
                    }
                }
                _likedPosts.value = likedSet

                _uiState.value = FeedUiState.Success(posts)
            } catch (e: Exception) {
                _uiState.value = FeedUiState.Error(
                    e.message ?: "Failed to load feed"
                )
            }
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val wasLiked = _likedPosts.value.contains(postId)

                // Optimistic update
                _likedPosts.value = if (wasLiked) {
                    _likedPosts.value - postId
                } else {
                    _likedPosts.value + postId
                }

                val currentState = _uiState.value
                if (currentState is FeedUiState.Success) {
                    val updatedPosts = currentState.posts.map { post ->
                        if (post.id == postId) {
                            post.copy(
                                likes = if (wasLiked) post.likes - 1 else post.likes + 1
                            )
                        } else post
                    }
                    _uiState.value = FeedUiState.Success(updatedPosts)
                }

                if (wasLiked) {
                    likeRepository.unlikePost(postId, userId)
                } else {
                    likeRepository.likePost(postId, userId)
                }

            } catch (e: Exception) {
                loadFeed()
            }
        }
    }

    fun savePost(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                savedPostRepository.savePost(postId, userId)
                _savedPosts.value = _savedPosts.value + postId
            } catch (_: Exception) {
            }
        }
    }

    fun savePlaylistFromPost(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val state = _uiState.value
                if (state is FeedUiState.Success) {
                    val post = state.posts.find { it.id == postId }
                    post?.playlist?.let { playlist ->
                        playlistRepository.favoritePlaylist(userId, playlist.id)
                    }
                }
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    fun saveStyleFromPost(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val state = _uiState.value
                if (state is FeedUiState.Success) {
                    val post = state.posts.find { it.id == postId }
                    val outfitId = post?.outfitId
                    if (outfitId.isNullOrBlank()) {
                        return@launch
                    }
                    savedOutfitRepository.saveOutfit(outfitId, userId)
                }
            } catch (_: Exception) {
            }
        }
    }
}