package com.emirgasic.forecastfm.data.repository

import android.util.Log
import com.emirgasic.forecastfm.network.comment.CommentApi
import com.emirgasic.forecastfm.network.comment.CommentResponse

class CommentRepository(
    private val commentApi: CommentApi,
    private val userRepository: UserRepository
) {

    suspend fun getComments(
        postId: String
    ): List<CommentResponse> {
        Log.d("CommentRepo", "📤 getComments called for post: $postId")
        return commentApi.getComments(postId)
    }

    suspend fun createComment(
        postId: String,
        text: String
    ): CommentResponse {
        Log.d("CommentRepo", "📤 createComment called: postId=$postId, text=$text")

        val user = userRepository.getCurrentUser()
        Log.d("CommentRepo", "📤 Current user: ${user.id}")

        return commentApi.createComment(
            userId = user.id,
            postId = postId,
            text = text
        )
    }

    suspend fun createCommentWithUserId(
        userId: String,
        postId: String,
        text: String
    ): CommentResponse {
        Log.d("CommentRepo", "📤 createCommentWithUserId: userId=$userId, postId=$postId, text=$text")
        return commentApi.createComment(
            userId = userId,
            postId = postId,
            text = text
        )
    }
}