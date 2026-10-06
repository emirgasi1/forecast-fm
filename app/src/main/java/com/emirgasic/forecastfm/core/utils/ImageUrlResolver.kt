package com.emirgasic.forecastfm.core.utils

import com.emirgasic.forecastfm.network.ApiClient


fun resolveImageUrl(path: String?): String? {
    if (path.isNullOrBlank()) return null
    return if (path.startsWith("http://") || path.startsWith("https://")) {
        path
    } else {
        "${ApiClient.baseUrl()}$path"
    }
}


fun resolveImageUrlOr(path: String?, fallback: String): String {
    return resolveImageUrl(path) ?: fallback
}