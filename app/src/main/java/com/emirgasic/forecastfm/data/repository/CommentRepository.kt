package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Comment
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.network.comment.CommentApi
import com.emirgasic.forecastfm.network.comment.CommentResponse


class CommentRepository(
    private val commentApi: CommentApi,
    private val userRepository: UserRepository
) {

    suspend fun getComments(
        postId: String
    ): List<CommentResponse> {

        return commentApi.getComments(postId)
    }


    suspend fun createComment(
        postId: String,
        text: String
    ): CommentResponse {

        val user = userRepository.getCurrentUser()

        return commentApi.createComment(
            userId = user.id,
            postId = postId,
            text = text
        )
    }
}