package com.emirgasic.forecastfm.network.comment

import android.util.Log
import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
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
    suspend fun likeComment(commentId: String, userId: String): Int {
        val response = ApiClient.client.post(
            "${ApiClient.baseUrl()}/api/comments/$commentId/like"
        ) {
            header("User-Id", userId)
        }
        val body: Map<String, Int> = response.body()
        return body["likes"] ?: 0
    }

    suspend fun unlikeComment(commentId: String, userId: String): Int {
        val response = ApiClient.client.delete(
            "${ApiClient.baseUrl()}/api/comments/$commentId/like"
        ) {
            header("User-Id", userId)
        }
        val body: Map<String, Int> = response.body()
        return body["likes"] ?: 0
    }
}