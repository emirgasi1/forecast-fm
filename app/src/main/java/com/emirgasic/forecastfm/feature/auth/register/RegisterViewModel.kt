package com.emirgasic.forecastfm.feature.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.utils.EmailValidator
import com.emirgasic.forecastfm.data.repository.AuthRepository
import com.emirgasic.forecastfm.data.repository.RegisterResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository,
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

    private val _checkMark = MutableStateFlow(false)
    val checkMark: StateFlow<Boolean> = _checkMark.asStateFlow()

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

    fun updateCheckMark(value: Boolean) {
        _checkMark.value = value
    }

    fun register() {
        if (email.value.isBlank()) {
            _errorMessage.value = "Email is required"
            return
        }
        if (!EmailValidator.isValid(email.value)) {
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
                when (val result = authRepository.register(
                    email = email.value,
                    username = username.value,
                    password = password.value,
                    bio = null
                )) {
                    is RegisterResult.Success -> {
                        Log.d("Register", "Registration successful for user: ${result.response.user.email}")

                        tokenManager.saveTokens(
                            token = result.response.token,
                            refreshToken = result.response.refreshToken,
                            userId = result.response.user.id,
                            email = result.response.user.email
                        )

                        _isLoading.value = false
                        onRegisterSuccess()
                    }

                    is RegisterResult.Error -> {
                        Log.e("Register", "Registration failed: ${result.message}")
                        _isLoading.value = false
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("connect") == true ->
                        "Cannot connect to server. Please check your connection."
                    else -> "Registration failed: ${e.message}"
                }
                Log.e("Register", "Registration error", e)
            }
        }
    }
}