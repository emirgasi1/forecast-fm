package com.emirgasic.forecastfm.feature.comments

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.R
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

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    fun loadComments(postId: String) {
        viewModelScope.launch {
            try {
                Log.d("CommentsVM", "📥 Loading comments for post: $postId")
                val commentResponses = commentRepository.getComments(postId)

                _comments.value = commentResponses.map { response ->
                    Comment(
                        id = response.id,
                        user = User(
                            id = response.userId,
                            username = "User",
                            bio = "",
                            profileImage = R.drawable.profile_picture,
                            favoriteLocation = "",
                            likes = 0,
                            posts = 0,
                            saved = 0
                        ),
                        text = response.text,
                        time = response.createdAt,
                        likes = response.likes
                    )
                }
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

                val newComment = Comment(
                    id = response.id,
                    user = User(
                        id = response.userId,
                        username = "User",
                        bio = "",
                        profileImage = R.drawable.profile_picture,
                        favoriteLocation = "",
                        likes = 0,
                        posts = 0,
                        saved = 0
                    ),
                    text = response.text,
                    time = response.createdAt,
                    likes = response.likes
                )

                _comments.value = _comments.value + newComment
                Log.d("CommentsVM", "✅ Comment added, total: ${_comments.value.size}")

            } catch (e: Exception) {
                Log.e("CommentsVM", "❌ Error adding comment: ${e.message}", e)
            }
        }
    }
}