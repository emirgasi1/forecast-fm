package com.emirgasic.forecastfm.core.utils

import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.network.youtube.YouTubeVideo

object YouTubeMapper {
    fun videoToMusic(video: YouTubeVideo): Music {
        return Music(
            id = video.id?.videoId ?: "",
            title = video.snippet?.title ?: "Unknown",
            artist = video.snippet?.channelTitle ?: "Unknown Artist",
            duration = 0,
            albumImageUrl = video.snippet?.thumbnails?.high?.url
                ?: video.snippet?.thumbnails?.medium?.url
        )
    }

    fun videosToPlaylist(
        videos: List<YouTubeVideo>,
        title: String = "YouTube Search Results",
        genre: String = "",
        mood: String = "",
        weather: String = "",
        temperature: String = "",
        location: String = ""
    ): Playlist {
        val songs = videos.map { videoToMusic(it) }

        return Playlist(
            id = "youtube_${System.currentTimeMillis()}",
            title = title,
            genre = genre,
            mood = mood,
            albumImageUrl = songs.firstOrNull()?.albumImageUrl,
            weather = weather,
            temperature = temperature,
            location = location,
            songs = songs,
            likes = 0,
            spotifyUrl = null,
            youtubeUrl = null
        )
    }
}