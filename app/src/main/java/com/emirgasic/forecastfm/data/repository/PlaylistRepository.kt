package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.network.playlist.PlaylistApi

class PlaylistRepository(
    private val playlistApi: PlaylistApi
) {

    suspend fun getPlaylists(): List<Playlist> {

        println("REPOSITORY: GET PLAYLISTS START")

        val responses = playlistApi.getPlaylists()

        println("REPOSITORY: RESPONSES SIZE = ${responses.size}")

        val playlists = responses.map { response ->

            Playlist(
                id = response.id,
                title = response.title,
                genre = response.genre,
                mood = response.mood,
                albumImageUrl = response.albumImageUrl,
                weather = response.weather,
                temperature = response.temperature,
                location = response.location,
                songs = response.songs.map { song ->
                    Music(
                        id = song.id,
                        title = song.title,
                        artist = song.artist,
                        duration = song.duration,
                        albumImageUrl = response.albumImageUrl
                    )
                },
                likes = response.likes,
                spotifyUrl = response.spotifyUrl,
                youtubeUrl = response.youtubeUrl
            )
        }

        println("REPOSITORY: PLAYLISTS MAPPED = ${playlists.size}")

        return playlists
    }

    suspend fun getPlaylist(
        id: String
    ): Playlist {

        val response = playlistApi.getPlaylist(id)

        return Playlist(
            id = response.id,
            title = response.title,
            genre = response.genre,
            mood = response.mood,
            albumImageUrl = response.albumImageUrl,
            weather = response.weather,
            temperature = response.temperature,
            location = response.location,
            songs = response.songs.map { song ->

                Music(
                    id = song.id,
                    title = song.title,
                    artist = song.artist,
                    duration = song.duration,
                    albumImageUrl = response.albumImageUrl
                )
            },
            likes = response.likes,
            spotifyUrl=response.spotifyUrl,
            youtubeUrl=response.youtubeUrl
        )
    }

    suspend fun favoritePlaylist(
        userId: String,
        playlistId: String
    ) {
        playlistApi.favoritePlaylist(
            userId = userId,
            playlistId = playlistId
        )
    }

    suspend fun unfavoritePlaylist(
        userId: String,
        playlistId: String
    ) {
        playlistApi.unfavoritePlaylist(
            userId = userId,
            playlistId = playlistId
        )
    }

    suspend fun getFavoritePlaylistIds(
        userId: String
    ):List<String>{

        return playlistApi.getFavoritePlaylistIds(
            userId=userId
        )
    }
}