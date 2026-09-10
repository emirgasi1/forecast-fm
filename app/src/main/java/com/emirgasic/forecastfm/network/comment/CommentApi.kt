package com.emirgasic.forecastfm.network.comment

import android.util.Log
import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class CommentApi {

    suspend fun getComments(
        postId: String
    ): List<CommentResponse> {
        Log.d("CommentApi", "📥 Getting comments for post: $postId")
        return ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/posts/$postId/comments"
        ).body()
    }

    suspend fun createComment(
        userId: String,
        postId: String,
        text: String
    ): CommentResponse {
        Log.d("CommentApi", "📤 Creating comment: userId=$userId, postId=$postId, text=$text")

        val response: CommentResponse = ApiClient.client.post(
            "${ApiClient.baseUrl()}/api/comments"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                CreateCommentRequest(
                    userId = userId,
                    postId = postId,
                    text = text
                )
            )
        }.body()

        Log.d("CommentApi", "📥 Response: $response")
        return response
    }
}