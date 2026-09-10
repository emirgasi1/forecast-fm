package com.emirgasic.forecastfm.data.repository



import com.emirgasic.forecastfm.network.user.UserApi
import com.emirgasic.forecastfm.network.user.UserResponse

class UserRepository(
    private val userApi: UserApi
) {

    suspend fun getCurrentUser(): UserResponse {
        return userApi.getUser(
            "d0bb864c-e207-4b4e-a0a7-102d10322ee3"
        )
    }

    suspend fun getUser(userId: String): UserResponse? {
        return try {
            userApi.getUser(userId)
        } catch (e: Exception) {
            null
        }
    }
}