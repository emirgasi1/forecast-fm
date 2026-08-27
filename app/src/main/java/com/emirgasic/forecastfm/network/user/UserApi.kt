package com.emirgasic.forecastfm.network.user

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class UserApi {

    suspend fun getUser(id: String): UserResponse {
        return ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/users/$id"
        ).body()
    }
}