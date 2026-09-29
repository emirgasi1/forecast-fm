package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.auth.request.ForgotPasswordRequest
import com.emirgasic.forecastfm.network.auth.request.RegisterRequest
import com.emirgasic.forecastfm.network.auth.response.AuthResponse
import com.emirgasic.forecastfm.network.auth.response.LoginRequest
import com.emirgasic.forecastfm.network.auth.response.RegisterResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

class AuthRepository(
    private val client: HttpClient = ApiClient.client,
    private val baseUrl: String = ApiClient.baseUrl()
) {

    suspend fun login(email: String, password: String): LoginResult {
        val response = client.post("$baseUrl/auth/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(LoginRequest(email, password))
        }

        return if (response.status.value == 200) {
            val authResponse: AuthResponse = response.body()
            LoginResult.Success(authResponse)
        } else {
            val errorBody: String = response.body()
            LoginResult.Error(
                if (errorBody.contains("Invalid credentials")) "Invalid email or password"
                else "Login failed. Please try again."
            )
        }
    }

    suspend fun register(
        email: String,
        username: String,
        password: String,
        bio: String? = null
    ): RegisterResult {
        val response = client.post("$baseUrl/auth/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                RegisterRequest(
                    email = email,
                    username = username,
                    password = password,
                    bio = bio
                )
            )
        }

        return when (response.status.value) {
            200, 201 -> {
                val registerResponse: RegisterResponse = response.body()
                RegisterResult.Success(registerResponse)
            }
            409 -> RegisterResult.Error("Email or username already taken")
            400 -> RegisterResult.Error("Invalid input. Please check your details.")
            else -> RegisterResult.Error("Registration failed. Please try again.")
        }
    }

    suspend fun forgotPassword(email: String): ForgotPasswordResult {
        val response = client.post("$baseUrl/auth/forgot-password") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(ForgotPasswordRequest(email))
        }

        return when (response.status.value) {
            200 -> ForgotPasswordResult.Success
            404 -> ForgotPasswordResult.Error("Email not found")
            else -> ForgotPasswordResult.Error("Failed to send reset link. Please try again.")
        }
    }
}

sealed interface LoginResult {
    data class Success(val response: AuthResponse) : LoginResult
    data class Error(val message: String) : LoginResult
}

sealed interface RegisterResult {
    data class Success(val response: RegisterResponse) : RegisterResult
    data class Error(val message: String) : RegisterResult
}

sealed interface ForgotPasswordResult {
    data object Success : ForgotPasswordResult
    data class Error(val message: String) : ForgotPasswordResult
}