package com.emirgasic.forecastfm.network.youtube

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import com.emirgasic.forecastfm.network.ApiClient

class YouTubeApi {
    suspend fun searchVideos(query: String, apiKey: String): YouTubeSearchResponse {
        return try {
            Log.d("YouTubeDebug", "🌐 API call START")
            Log.d("YouTubeDebug", "🌐 Query: '$query'")
            Log.d("YouTubeDebug", "🌐 API Key: ${apiKey.take(10)}...")

            val response = ApiClient.client.get {
                url("https://www.googleapis.com/youtube/v3/search")
                parameter("part", "snippet")
                parameter("q", query)
                parameter("type", "video")
                parameter("maxResults", "10")
                parameter("key", apiKey)
            }

            Log.d("YouTubeDebug", "🌐 Response status: ${response.status}")

            val body = response.body<String>()
            Log.d("YouTubeDebug", "🌐 Raw response length: ${body?.length}")
            Log.d("YouTubeDebug", "🌐 Raw response: ${body?.take(500)}")

            val parsed = response.body<YouTubeSearchResponse>()
            Log.d("YouTubeDebug", "🌐 Parsed items: ${parsed.items?.size}")

            parsed
        } catch (e: Exception) {
            Log.e("YouTubeDebug", "❌ API error: ${e.message}", e)
            throw e
        }
    }
}