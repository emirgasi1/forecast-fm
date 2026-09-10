package com.emirgasic.forecastfm.network.youtube

import kotlinx.serialization.Serializable

@Serializable
data class YouTubeSearchResponse(
    val items: List<YouTubeVideo>? = null
)

@Serializable
data class YouTubeVideo(
    val id: YouTubeVideoId? = null,
    val snippet: YouTubeSnippet? = null
)

@Serializable
data class YouTubeVideoId(
    val videoId: String = ""
)

@Serializable
data class YouTubeSnippet(
    val title: String = "",
    val channelTitle: String = "",
    val description: String = "",
    val thumbnails: YouTubeThumbnails? = null
)

@Serializable
data class YouTubeThumbnails(
    val default: YouTubeThumbnail? = null,
    val medium: YouTubeThumbnail? = null,
    val high: YouTubeThumbnail? = null
)

@Serializable
data class YouTubeThumbnail(
    val url: String = ""
)