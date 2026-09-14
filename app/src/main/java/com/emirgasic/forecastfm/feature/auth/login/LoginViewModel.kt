package com.emirgasic.forecastfm.feature.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.auth.response.AuthResponse
import com.emirgasic.forecastfm.network.auth.response.LoginRequest
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val tokenManager: TokenManager,
    private val onLoginSuccess: () -> Unit
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateEmail(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun updatePassword(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun login() {
        if (email.value.isBlank() || password.value.isBlank()) {
            _errorMessage.value = "Email and password are required"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("Login", "Sending login request...")
                Log.d("Login", "Email: ${email.value}")

                val response = ApiClient.client.post("${ApiClient.baseUrl()}/auth/login") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(LoginRequest(email.value, password.value))
                }

                // Check response status
                if (response.status.value == 200) {
                    val authResponse: AuthResponse = response.body()
                    Log.d("Login", "Login successful for user: ${authResponse.user.email}")

                    tokenManager.saveTokens(
                        token = authResponse.token,
                        refreshToken = authResponse.refreshToken,
                        userId = authResponse.user.id,
                        email = authResponse.user.email
                    )

                    _isLoading.value = false
                    onLoginSuccess()
                } else {
                    val errorResponse = response.body<String>()
                    Log.e("Login", "Login failed with status: ${response.status.value}")
                    Log.e("Login", "Error response: $errorResponse")

                    _isLoading.value = false
                    _errorMessage.value = when {
                        errorResponse?.contains("Invalid credentials") == true -> "Invalid email or password"
                        else -> "Login failed. Please try again."
                    }
                }

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("connect") == true -> "Cannot connect to server. Please check your connection."
                    else -> "Login failed: ${e.message}"
                }
                Log.e("Login", "Error: ${e.message}", e)
            }
        }
    }
}