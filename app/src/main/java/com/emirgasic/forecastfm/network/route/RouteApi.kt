package com.emirgasic.forecastfm.network.route

import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class RouteApi {

    private val client = ApiClient.client

    suspend fun getRoute(request: RouteRequest): RouteResponse {
        return client
            .post("${ApiClient.baseUrl()}/api/route") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            .body()
    }
}