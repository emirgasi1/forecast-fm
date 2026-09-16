package com.emirgasic.forecastfm.feature.comments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.data.repository.CommentRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.comment.CommentApi
import com.emirgasic.forecastfm.network.user.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CommentsViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val commentRepository = CommentRepository(
        commentApi = CommentApi(),
        userRepository = UserRepository(
            userApi = UserApi()
        )
    )
    private val _likedComments = MutableStateFlow<Set<String>>(emptySet())
    val likedComments: StateFlow<Set<String>> = _likedComments.asStateFlow()
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    fun loadComments(postId: String) {
        viewModelScope.launch {
            try {
                Log.d("CommentsVM", "📥 Loading comments for post: $postId")
                val commentResponses = commentRepository.getComments(postId)

                val mapped = commentResponses.map { response ->
                    val userResponse = try {
                        commentRepository.getUserForComment(response.userId)
                    } catch (e: Exception) {
                        null
                    }

                    val username = userResponse?.username ?: "User"
                    val profileImage = if (!userResponse?.profileImageUrl.isNullOrBlank()) {
                        "${com.emirgasic.forecastfm.network.ApiClient.baseUrl()}${userResponse!!.profileImageUrl}"
                    } else {
                        "https://picsum.photos/seed/$username/200/200"
                    }

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
                Log.d("CommentsVM", "✅ Loaded ${_comments.value.size} comments")
            } catch (e: Exception) {
                Log.e("CommentsVM", "❌ Error loading comments: ${e.message}", e)
                _comments.value = emptyList()
            }
        }
    }

    fun addComment(
        postId: String,
        text: String
    ) {
        Log.d("CommentsVM", "🔵 addComment called! postId=$postId, text=$text")
        if (text.isBlank()) {
            Log.d("CommentsVM", "⚠️ Text is blank, returning")
            return
        }

        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId == null) {
                    Log.e("CommentsVM", "❌ User not logged in")
                    return@launch
                }

                Log.d("CommentsVM", "📤 Calling commentRepository.createCommentWithUserId...")
                val response = commentRepository.createCommentWithUserId(
                    userId = userId,
                    postId = postId,
                    text = text
                )
                Log.d("CommentsVM", "📥 Response: $response")

                val userResponse = try {
                    commentRepository.getUserForComment(userId)
                } catch (e: Exception) {
                    null
                }

                val username = userResponse?.username ?: "User"
                val profileImage = if (!userResponse?.profileImageUrl.isNullOrBlank()) {
                    "${com.emirgasic.forecastfm.network.ApiClient.baseUrl()}${userResponse!!.profileImageUrl}"
                } else {
                    "https://picsum.photos/seed/$username/200/200"
                }

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
                Log.d("CommentsVM", "✅ Comment added, total: ${_comments.value.size}")

            } catch (e: Exception) {
                Log.e("CommentsVM", "❌ Error adding comment: ${e.message}", e)
            }


        }
    }
    private fun formatDate(iso: String): String {
        return try {
            val instant = java.time.Instant.parse(iso)
            java.time.format.DateTimeFormatter
                .ofPattern("d MMM yyyy")
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant)
        } catch (e: Exception) {
            iso
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
            } catch (e: Exception) {
                Log.e("CommentsVM", "❌ toggleLike failed: ${e.message}", e)
            }
        }
    }
}