package com.emirgasic.forecastfm.network.comment

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

        return ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/posts/$postId/comments"
        ).body()
    }


    suspend fun createComment(
        userId: String,
        postId: String,
        text: String
    ): CommentResponse {

        return ApiClient.client.post(
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
    }
}