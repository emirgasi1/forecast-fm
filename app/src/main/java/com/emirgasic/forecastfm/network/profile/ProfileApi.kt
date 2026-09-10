package com.emirgasic.forecastfm.network.profile

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ProfileApi {

    suspend fun getProfile(
        userId: String
    ): ProfileResponse {
        println("📥 Fetching profile for userId: $userId")
        val response: ProfileResponse = ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/users/$userId/profile"
        ).body()
        println("📥 Profile response: posts count = ${response.posts.size}")
        return response
    }
}