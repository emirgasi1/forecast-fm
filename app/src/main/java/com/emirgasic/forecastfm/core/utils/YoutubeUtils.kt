package com.emirgasic.forecastfm.core.utils

object YouTubeUtils {

    fun extractPlaylistId(url: String?): String? {
        if (url.isNullOrBlank()) return null

        return try {
            val query = url.substringAfter("?", "")
            query.split("&")
                .firstOrNull { it.startsWith("list=") }
                ?.removePrefix("list=")
        } catch (e: Exception) {
            null
        }
    }
}