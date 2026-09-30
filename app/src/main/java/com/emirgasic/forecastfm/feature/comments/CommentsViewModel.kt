package com.emirgasic.forecastfm.feature.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.data.repository.CommentRepository
import com.emirgasic.forecastfm.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val tokenManager: TokenManager,
    private val commentRepository: CommentRepository
) : ViewModel() {

    private val _likedComments = MutableStateFlow<Set<String>>(emptySet())
    val likedComments: StateFlow<Set<String>> = _likedComments.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    fun loadComments(postId: String) {
        viewModelScope.launch {
            try {
                val commentResponses = commentRepository.getComments(postId)

                val mapped = commentResponses.map { response ->
                    val userResponse = try {
                        commentRepository.getUserForComment(response.userId)
                    } catch (_: Exception) {
                        null
                    }

                    val username = userResponse?.username ?: "User"
                    val profileImage = buildProfileImage(
                        username = username,
                        rawUrl = userResponse?.profileImageUrl
                    )

                    Comment(
                        id = response.id,
                        user = User(
                            id = response.userId,
                            username = username,
                            bio = userResponse?.bio ?: "",
                            profileImage = profileImage,
                            favoriteLocation = userResponse?.favoriteLocation ?: "",
                            likes = 0,
                            posts = 0,
                            saved = 0
                        ),
                        text = response.text,
                        time = formatDate(response.createdAt),
                        likes = response.likes
                    )
                }

                _comments.value = mapped
            } catch (_: Exception) {
                _comments.value = emptyList()
            }
        }
    }

    fun addComment(postId: String, text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch

                val response = commentRepository.createCommentWithUserId(
                    userId = userId,
                    postId = postId,
                    text = text
                )

                val userResponse = try {
                    commentRepository.getUserForComment(userId)
                } catch (_: Exception) {
                    null
                }

                val username = userResponse?.username ?: "User"
                val profileImage = buildProfileImage(
                    username = username,
                    rawUrl = userResponse?.profileImageUrl
                )

                val newComment = Comment(
                    id = response.id,
                    user = User(
                        id = response.userId,
                        username = username,
                        bio = userResponse?.bio ?: "",
                        profileImage = profileImage,
                        favoriteLocation = userResponse?.favoriteLocation ?: "",
                        likes = 0,
                        posts = 0,
                        saved = 0
                    ),
                    text = response.text,
                    time = formatDate(response.createdAt),
                    likes = response.likes
                )

                _comments.value = _comments.value + newComment
            } catch (_: Exception) {
                // Silent failure — the comment simply isn't appended
            }
        }
    }

    fun toggleLike(commentId: String) {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first() ?: return@launch

                val isLiked = _likedComments.value.contains(commentId)

                val newCount = if (isLiked) {
                    commentRepository.unlikeComment(commentId, userId)
                } else {
                    commentRepository.likeComment(commentId, userId)
                }

                _likedComments.value = if (isLiked) {
                    _likedComments.value - commentId
                } else {
                    _likedComments.value + commentId
                }

                _comments.value = _comments.value.map { c ->
                    if (c.id == commentId) c.copy(likes = newCount) else c
                }
            } catch (_: Exception) {
                // Silent failure
            }
        }
    }

    private fun buildProfileImage(username: String, rawUrl: String?): String {
        return if (!rawUrl.isNullOrBlank()) {
            "${ApiClient.baseUrl()}$rawUrl"
        } else {
            "https://picsum.photos/seed/$username/200/200"
        }
    }

    private fun formatDate(iso: String): String {
        return try {
            val instant = java.time.Instant.parse(iso)
            java.time.format.DateTimeFormatter
                .ofPattern("d MMM yyyy")
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant)
        } catch (_: Exception) {
            iso
        }
    }
}