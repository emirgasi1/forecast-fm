package com.emirgasic.forecastfm.network.like

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class LikeApi {

    suspend fun likePost(postId: String, userId: String) {
        ApiClient.client.post("${ApiClient.baseUrl()}/api/posts/$postId/like") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("userId" to userId))
        }
    }

    suspend fun unlikePost(postId: String, userId: String) {
        ApiClient.client.delete("${ApiClient.baseUrl()}/api/posts/$postId/like") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("userId" to userId))
        }
    }

    suspend fun isPostLiked(postId: String, userId: String): Boolean {
        val response: Boolean = ApiClient.client.get("${ApiClient.baseUrl()}/api/posts/$postId/like") {
            parameter("userId", userId)
        }.body()
        return response
    }

    suspend fun getLikeCount(postId: String): Int {
        val response: Int = ApiClient.client.get("${ApiClient.baseUrl()}/api/posts/$postId/likes").body()
        return response
    }
}