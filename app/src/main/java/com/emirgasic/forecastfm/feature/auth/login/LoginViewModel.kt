package com.emirgasic.forecastfm.feature.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.AuthRepository
import com.emirgasic.forecastfm.data.repository.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
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
                when (val result = authRepository.login(email.value, password.value)) {
                    is LoginResult.Success -> {
                        Log.d("Login", "Login successful for user: ${result.response.user.email}")

                        tokenManager.saveTokens(
                            token = result.response.token,
                            refreshToken = result.response.refreshToken,
                            userId = result.response.user.id,
                            email = result.response.user.email
                        )

                        _isLoading.value = false
                        onLoginSuccess()
                    }

                    is LoginResult.Error -> {
                        Log.e("Login", "Login failed: ${result.message}")
                        _isLoading.value = false
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("connect") == true ->
                        "Cannot connect to server. Please check your connection."
                    else -> "Login failed: ${e.message}"
                }
                Log.e("Login", "Error: ${e.message}", e)
            }
        }
    }
}