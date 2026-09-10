package com.emirgasic.forecastfm.feature.auth.forgotpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.auth.request.ForgotPasswordRequest
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val onSuccess: () -> Unit
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun updateEmail(value: String) {
        _email.value = value
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun sendResetLink() {
        if (email.value.isBlank()) {
            _errorMessage.value = "Email is required"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            _errorMessage.value = "Invalid email format"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                Log.d("ForgotPassword", "Sending reset link to: ${email.value}")

                val response = ApiClient.client.post("${ApiClient.baseUrl()}/auth/forgot-password") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(ForgotPasswordRequest(email.value))
                }

                if (response.status.value == 200) {
                    Log.d("ForgotPassword", "Reset link sent successfully")
                    _isLoading.value = false
                    _successMessage.value = "Password reset link sent to your email"

                    // Navigate back after delay
                    kotlinx.coroutines.delay(2000)
                    onSuccess()
                } else {
                    val errorResponse = response.body<String>()
                    Log.e("ForgotPassword", "Failed with status: ${response.status.value}")
                    _isLoading.value = false
                    _errorMessage.value = when {
                        response.status.value == 404 -> "Email not found"
                        else -> "Failed to send reset link. Please try again."
                    }
                }

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("connect") == true -> "Cannot connect to server. Please check your connection."
                    else -> "Failed to send reset link: ${e.message}"
                }
                Log.e("ForgotPassword", "Error: ${e.message}", e)
            }
        }
    }
}