package com.emirgasic.forecastfm.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ApiConfig {

    private const val DEFAULT_BASE_URL = "http://192.168.1.8:8080"

    private val _baseUrl = MutableStateFlow(DEFAULT_BASE_URL)
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    fun setBaseUrl(url: String) {
        val trimmed = url.trim().trimEnd('/')
        if (trimmed.isNotEmpty()) {
            _baseUrl.value = trimmed
        }
    }

    fun reset() {
        _baseUrl.value = DEFAULT_BASE_URL
    }

    fun current(): String = _baseUrl.value
}