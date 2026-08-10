package com.emirgasic.forecastfm.feature.auth.forgotpassword

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ForgotPasswordViewModel : ViewModel() {

    private val _email = MutableStateFlow("")

    val email: StateFlow<String> =
        _email.asStateFlow()


    fun updateEmail(value: String) {
        _email.value = value
    }


    fun resetPassword() {
        // Password reset will be implemented later
    }

}