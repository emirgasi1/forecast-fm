package com.emirgasic.forecastfm.network.post

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PostApi {

    suspend fun getPosts(): List<PostResponse> {
        return ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/posts"
        ).body()
    }

    suspend fun getPostsByUserId(
        userId: String
    ): List<PostResponse> {
        return ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/users/$userId/posts"
        ).body()
    }

    suspend fun createPost(
        userId: String,
        caption: String?,
        imageUrl: String?
    ): PostResponse {
        return ApiClient.client.post("${ApiClient.baseUrl()}/api/posts") {
            contentType(ContentType.Application.Json)
            setBody(
                CreatePostRequest(
                    userId = userId,
                    caption = caption,
                    imageUrl = imageUrl
                )
            )
        }.body()
    }

    suspend fun savePost(postId: String, userId: String) {
        ApiClient.client.post("${ApiClient.baseUrl()}/api/posts/$postId/save") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("userId" to userId))
        }
    }

    suspend fun unsavePost(postId: String, userId: String) {
        ApiClient.client.delete("${ApiClient.baseUrl()}/api/posts/$postId/save") {
            parameter("userId", userId)
        }
    }

    suspend fun isPostSaved(postId: String, userId: String): Boolean {
        val response: Map<String, Boolean> = ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/posts/$postId/save"
        ) {
            parameter("userId", userId)
        }.body()
        return response["saved"] ?: false
    }

    suspend fun getSavedPosts(userId: String): List<PostResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/posts/saved") {
            header("User-Id", userId)
        }.body()
    }
}