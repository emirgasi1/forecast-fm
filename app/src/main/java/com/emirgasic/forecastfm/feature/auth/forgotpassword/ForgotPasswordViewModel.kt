package com.emirgasic.forecastfm.feature.auth.forgotpassword

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.utils.EmailValidator
import com.emirgasic.forecastfm.data.repository.AuthRepository
import com.emirgasic.forecastfm.data.repository.ForgotPasswordResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository,
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
        if (!EmailValidator.isValid(email.value)) {
            _errorMessage.value = "Invalid email format"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null

            try {
                Log.d("ForgotPassword", "Sending reset link")

                when (val result = authRepository.forgotPassword(email.value)) {
                    is ForgotPasswordResult.Success -> {
                        Log.d("ForgotPassword", "Reset link sent successfully")
                        _isLoading.value = false
                        _successMessage.value = "Password reset link sent to your email"

                        delay(2000)
                        onSuccess()
                    }

                    is ForgotPasswordResult.Error -> {
                        Log.e("ForgotPassword", "Failed: ${result.message}")
                        _isLoading.value = false
                        _errorMessage.value = result.message
                    }
                }

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("connect") == true ->
                        "Cannot connect to server. Please check your connection."
                    else -> "Failed to send reset link: ${e.message}"
                }
                Log.e("ForgotPassword", "Error: ${e.message}", e)
            }
        }
    }
}