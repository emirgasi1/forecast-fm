package com.emirgasic.forecastfm.network.playlist

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class PlaylistApi {

    suspend fun getPlaylists(): List<PlaylistResponse> {

        val response =
            ApiClient.client.get(
                "${ApiClient.baseUrl()}/api/playlists"
            )

        val body =
            response.bodyAsText()

        println("PLAYLIST BODY: $body")

        return Json.decodeFromString(body)
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
    suspend fun getSavedPlaylists(userId: String): List<PlaylistResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/playlists/saved") {
            header("User-Id", userId)
        }.body()
    }
    suspend fun updatePlaylistImage(
        playlistId: String,
        imageUrl: String
    ) {
        ApiClient.client.put("${ApiClient.baseUrl()}/api/playlists/$playlistId/image") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("imageUrl" to imageUrl))
        }
    }

}
