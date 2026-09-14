package com.emirgasic.forecastfm.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.theme.AppTheme
import com.emirgasic.forecastfm.core.theme.ThemeManager
import com.emirgasic.forecastfm.network.ApiClient
import com.emirgasic.forecastfm.network.ApiConfig
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _currentTheme = MutableStateFlow(ThemeManager.selectedTheme.value)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private val _currentApiUrl = MutableStateFlow(ApiConfig.current())
    val currentApiUrl: StateFlow<String> = _currentApiUrl.asStateFlow()

    private val _userId = MutableStateFlow("N/A")
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _userEmail = MutableStateFlow("N/A")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _backendStatus = MutableStateFlow("Not checked")
    val backendStatus: StateFlow<String> = _backendStatus.asStateFlow()

    private val _currentHour = MutableStateFlow(ThemeManager.currentHour())
    val currentHour: StateFlow<Int> = _currentHour.asStateFlow()

    init {
        loadUserInfo()
    }

    fun setTheme(theme: AppTheme) {
        ThemeManager.setTheme(theme)
        _currentTheme.value = theme
        android.util.Log.d("AdminTheme", "Theme set to $theme")
    }

    fun setApiUrl(url: String) {
        ApiConfig.setBaseUrl(url)
        _currentApiUrl.value = ApiConfig.current()
    }

    fun resetApiUrl() {
        ApiConfig.reset()
        _currentApiUrl.value = ApiConfig.current()
    }

    fun pingBackend() {
        viewModelScope.launch {
            _backendStatus.value = "Pinging..."
            try {
                val start = System.currentTimeMillis()
                val client = ApiClient.client
                val response = client.get(ApiConfig.current())
                val elapsed = System.currentTimeMillis() - start
                _backendStatus.value = "OK (${response.status.value}) in ${elapsed}ms"
            } catch (e: Exception) {
                _backendStatus.value = "Failed: ${e.message ?: "unknown error"}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            tokenManager.getUserId().collect { id ->
                _userId.value = id ?: "N/A"
            }
        }
        viewModelScope.launch {
            tokenManager.getUserEmail().collect { email ->
                _userEmail.value = email ?: "N/A"
            }
        }
    }
}