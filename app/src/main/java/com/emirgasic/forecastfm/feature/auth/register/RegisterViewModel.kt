package com.emirgasic.forecastfm.feature.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.auth.request.RegisterRequest
import com.emirgasic.forecastfm.network.auth.response.RegisterResponse
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

class RegisterViewModel(
    private val tokenManager: TokenManager,
    private val onRegisterSuccess: () -> Unit
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword.asStateFlow()

    private val _checkMark = MutableStateFlow(false)  // ← Add this
    val checkMark: StateFlow<Boolean> = _checkMark.asStateFlow()  // ← Add this

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateEmail(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun updateUsername(value: String) {
        _username.value = value
        _errorMessage.value = null
    }

    fun updatePassword(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun updateConfirmPassword(value: String) {
        _confirmPassword.value = value
        _errorMessage.value = null
    }

    fun updateCheckMark(value: Boolean) {  // ← Add this
        _checkMark.value = value
    }

    fun register() {
        if (email.value.isBlank()) {
            _errorMessage.value = "Email is required"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            _errorMessage.value = "Invalid email format"
            return
        }
        if (username.value.isBlank()) {
            _errorMessage.value = "Username is required"
            return
        }
        if (username.value.length < 3) {
            _errorMessage.value = "Username must be at least 3 characters"
            return
        }
        if (password.value.isBlank()) {
            _errorMessage.value = "Password is required"
            return
        }
        if (password.value.length < 8) {
            _errorMessage.value = "Password must be at least 8 characters"
            return
        }
        if (password.value != confirmPassword.value) {
            _errorMessage.value = "Passwords do not match"
            return
        }
        if (!checkMark.value) {
            _errorMessage.value = "You must agree to the Terms & Privacy"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d("Register", "Registering user: ${email.value}")

                val response = ApiClient.client.post("${ApiClient.baseUrl()}/auth/register") {
                    header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(
                        RegisterRequest(
                            email = email.value,
                            username = username.value,
                            password = password.value,
                            bio = null
                        )
                    )
                }

                if (response.status.value == 200 || response.status.value == 201) {
                    val registerResponse: RegisterResponse = response.body()
                    Log.d("Register", "Registration successful: ${registerResponse.id}")
                    _isLoading.value = false
                    onRegisterSuccess()
                } else {
                    val errorResponse = response.body<String>()
                    Log.e("Register", "Registration failed with status: ${response.status.value}")
                    _isLoading.value = false
                    _errorMessage.value = when {
                        response.status.value == 409 -> "Email or username already taken"
                        response.status.value == 400 -> "Invalid input. Please check your details."
                        else -> "Registration failed. Please try again."
                    }
                }

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("connect") == true -> "Cannot connect to server. Please check your connection."
                    else -> "Registration failed: ${e.message}"
                }
                Log.e("Register", "Registration error", e)
            }
        }
    }
}