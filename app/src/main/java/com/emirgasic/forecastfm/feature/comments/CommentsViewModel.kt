package com.emirgasic.forecastfm.feature.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.data.repository.CommentRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.comment.CommentApi
import com.emirgasic.forecastfm.network.user.UserApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class CommentsViewModel : ViewModel() {

    private val commentRepository = CommentRepository(
        commentApi = CommentApi(),
        userRepository = UserRepository(
            userApi = UserApi()
        )
    )

    private val _comments =
        MutableStateFlow<List<Comment>>(emptyList())

    val comments: StateFlow<List<Comment>> =
        _comments.asStateFlow()


    fun loadComments(postId: String) {

        viewModelScope.launch {

            try {

                val commentResponses =
                    commentRepository.getComments(postId)

                _comments.value =
                    commentResponses.map { response ->

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

            } catch (e: Exception) {

                println(
                    "COMMENTS VIEWMODEL ERROR: ${e.message}"
                )

                _comments.value = emptyList()
            }
        }
    }


    fun addComment(
        postId: String,
        text: String
    ) {

        if (text.isBlank()) return

        viewModelScope.launch {

            try {

                val response =
                    commentRepository.createComment(
                        postId = postId,
                        text = text
                    )

                val newComment = Comment(
                    id = response.id,

                    user = User(
                        id = response.userId,
                        username = "Emir",
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

                _comments.value =
                    _comments.value + newComment

            } catch (e: Exception) {

                println(
                    "COMMENTS VIEWMODEL ERROR: ${e.message}"
                )
            }
        }
    }
}