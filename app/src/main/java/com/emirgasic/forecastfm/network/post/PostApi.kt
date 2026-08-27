package com.emirgasic.forecastfm.network.post

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get

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
}