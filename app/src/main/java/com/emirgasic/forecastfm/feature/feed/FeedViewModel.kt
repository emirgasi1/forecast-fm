package com.emirgasic.forecastfm.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.FeedPost
import com.emirgasic.forecastfm.data.repository.CommentRepository
import com.emirgasic.forecastfm.data.repository.FeedRepository
import com.emirgasic.forecastfm.data.repository.LikeRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.PostRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedPostRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.comment.CommentApi
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.user.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class FeedViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val savedPostRepository = SavedPostRepository()
    private val savedOutfitRepository = SavedOutfitRepository()

    private val outfitRepository = OutfitRepository()


    private val userApi = UserApi()
    private val userRepository = UserRepository(userApi)
    private val postApi = PostApi()
    private val postRepository = PostRepository(postApi)
    private val playlistRepository = PlaylistRepository(PlaylistApi())
    private val commentApi = CommentApi()
    private val commentRepository = CommentRepository(commentApi, userRepository)
    private val likeRepository = LikeRepository()

    private val _likedPosts = MutableStateFlow<Set<String>>(emptySet())
    private val _savedPosts = MutableStateFlow<Set<String>>(emptySet())
    val savedPosts: StateFlow<Set<String>> = _savedPosts.asStateFlow()
    val likedPosts: StateFlow<Set<String>> = _likedPosts.asStateFlow()

    private val repository = FeedRepository(
        userRepository = userRepository,
        postRepository = postRepository,
        playlistRepository = playlistRepository,
        commentRepository = commentRepository
    )

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _uiState.value = FeedUiState.Loading

            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val posts = repository.getPosts(userId)

                // Check which posts are liked by the user
                val likedSet = mutableSetOf<String>()
                posts.forEach { post ->
                    try {
                        if (likeRepository.isPostLiked(post.id, userId)) {
                            likedSet.add(post.id)
                        }
                    } catch (e: Exception) {
                        // Ignore
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

                val isLiked = _likedPosts.value.contains(postId)

                // Update UI immediately (optimistic update)
                if (isLiked) {
                    _likedPosts.value = _likedPosts.value - postId
                    likeRepository.unlikePost(postId, userId)
                } else {
                    _likedPosts.value = _likedPosts.value + postId
                    likeRepository.likePost(postId, userId)
                }

                // Update like count in posts
                val currentState = _uiState.value
                if (currentState is FeedUiState.Success) {
                    val updatedPosts = currentState.posts.map { post ->
                        if (post.id == postId) {
                            post.copy(likes = if (isLiked) post.likes - 1 else post.likes + 1)
                        } else {
                            post
                        }
                    }
                    _uiState.value = FeedUiState.Success(updatedPosts)
                }

            } catch (e: Exception) {
                // Revert if API call fails
                loadFeed()
            }
        }
    }
    fun savePost(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                // Save post
                savedPostRepository.savePost(postId, userId)
                _savedPosts.value = _savedPosts.value + postId

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun savePlaylistFromPost(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                // Find the post and get its playlist
                val state = _uiState.value
                if (state is FeedUiState.Success) {
                    val post = state.posts.find { it.id == postId }
                    post?.playlist?.let { playlist ->
                        playlistRepository.favoritePlaylist(userId, playlist.id)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveStyleFromPost(postId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                // Find the post and save its style/outfit
                // For now, we'll save a default outfit or find by weather
                val state = _uiState.value
                if (state is FeedUiState.Success) {
                    val post = state.posts.find { it.id == postId }
                    // TODO: Get outfit based on post's weather condition
                    // For now, save the first outfit matching the weather
                    post?.weather?.condition?.let { weather ->
                        val outfits = outfitRepository.getOutfitsByWeather(weather)
                        outfits.firstOrNull()?.let { outfit ->
                            savedOutfitRepository.saveOutfit(userId, outfit.id)
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}