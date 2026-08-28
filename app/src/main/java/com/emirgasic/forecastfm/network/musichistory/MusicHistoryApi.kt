package com.emirgasic.forecastfm.network.musichistory

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.http.ContentType
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType

class MusicHistoryApi {

    suspend fun getHistory(
        userId: String
    ): List<MusicHistoryResponse> {

        println("API: GET HISTORY START")

        val response = ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/music-history/$userId"
        )

        println("API: GET HISTORY STATUS = ${response.status}")

        return response.body()
    }

    suspend fun addHistory(
        userId: String,
        playlistId: String
    ) {

        println("API: ADD HISTORY START")

        val response = ApiClient.client.post(
            "${ApiClient.baseUrl()}/api/music-history"
        ) {
            contentType(ContentType.Application.Json)

            setBody(
                AddMusicHistoryRequest(
                    userId = userId,
                    playlistId = playlistId
                )
            )
        }

        println("API: ADD HISTORY STATUS = ${response.status}")
        println("API: ADD HISTORY BODY = ${response.bodyAsText()}")
    }
}