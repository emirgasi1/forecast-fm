package com.emirgasic.forecastfm.network.playlist

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post

class PlaylistApi {

    suspend fun getPlaylists(): List<PlaylistResponse> {

        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/playlists")
            .body()
    }

    suspend fun getPlaylist(
        id: String
    ): PlaylistResponse {

        return ApiClient.client
            .get("${ApiClient.baseUrl()}/api/playlists/$id")
            .body()
    }
    suspend fun favoritePlaylist(
        userId: String,
        playlistId: String
    ) {
        ApiClient.client.post(
            "${ApiClient.baseUrl()}/api/playlists/$playlistId/favorite"
        ) {
            header("User-Id", userId)
        }
    }

    suspend fun unfavoritePlaylist(
        userId: String,
        playlistId: String
    ) {
        ApiClient.client.delete(
            "${ApiClient.baseUrl()}/api/playlists/$playlistId/favorite"
        ) {
            header("User-Id", userId)
        }
    }

    suspend fun getFavoritePlaylistIds(
        userId: String
    ): List<String> {

        return ApiClient.client
            .get(
                "${ApiClient.baseUrl()}/api/playlists/favorites"
            ) {
                header("User-Id", userId)
            }
            .body()
    }
}