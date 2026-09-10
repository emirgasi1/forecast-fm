package com.emirgasic.forecastfm.data.repository

import android.util.Log
import com.emirgasic.forecastfm.BuildConfig
import com.emirgasic.forecastfm.network.youtube.YouTubeApi
import com.emirgasic.forecastfm.network.youtube.YouTubeVideo

class YouTubeRepository(
    private val api: YouTubeApi = YouTubeApi()
) {
    private val apiKey = BuildConfig.YOUTUBE_API_KEY

    suspend fun searchVideos(query: String): List<YouTubeVideo> {
        return try {
            Log.d("YouTubeDebug", "API Key: ${apiKey.take(10)}...")
            Log.d("YouTubeDebug", "Searching for: $query")

            val response = api.searchVideos(query, apiKey)

            Log.d("YouTubeDebug", "Response received")
            Log.d("YouTubeDebug", "Items size: ${response.items?.size}")

            response.items ?: emptyList()
        } catch (e: Exception) {
            Log.e("YouTubeDebug", "Error: ${e.message}", e)
            throw e
        }
    }
}