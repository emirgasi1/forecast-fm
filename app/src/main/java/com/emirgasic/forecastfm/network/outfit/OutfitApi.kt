package com.emirgasic.forecastfm.network.outfit

import android.content.ContentResolver
import android.net.Uri
import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import java.io.InputStream

class OutfitApi {

    suspend fun getTrendingOutfits(): List<OutfitResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/outfits/trending").body()
    }

    suspend fun getOutfitsByWeather(weather: String): List<OutfitResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/outfits/weather/$weather").body()
    }

    suspend fun likeOutfit(outfitId: String) {
        ApiClient.client.post("${ApiClient.baseUrl()}/api/outfits/$outfitId/like") {
            setBody(emptyMap<String, String>())
        }
    }

    suspend fun saveOutfit(outfitId: String, userId: String) {
        ApiClient.client.post("${ApiClient.baseUrl()}/api/outfits/$outfitId/save") {
            header("User-Id", userId)
        }
    }

    suspend fun unsaveOutfit(outfitId: String, userId: String) {
        ApiClient.client.delete("${ApiClient.baseUrl()}/api/outfits/$outfitId/save") {
            header("User-Id", userId)
        }
    }

    suspend fun isOutfitSaved(outfitId: String, userId: String): Boolean {
        val response: Map<String, Boolean> = ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/outfits/$outfitId/save"
        ) {
            header("User-Id", userId)
        }.body()
        return response["saved"] ?: false
    }

    suspend fun getSavedOutfits(userId: String): List<OutfitResponse> {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/outfits/saved") {
            header("User-Id", userId)
        }.body()
    }

    suspend fun getOutfitById(id: String): OutfitResponse {
        return ApiClient.client.get("${ApiClient.baseUrl()}/api/outfits/$id").body()
    }

    suspend fun uploadOutfitImage(
        contentResolver: ContentResolver,
        imageUri: String
    ): String {
        val uri = Uri.parse(imageUri)
        val inputStream: InputStream = contentResolver.openInputStream(uri)
            ?: throw Exception("Failed to open image")

        val bytes = inputStream.use { it.readBytes() }
        val fileName = uri.lastPathSegment ?: "image.jpg"

        val response = ApiClient.client.post("${ApiClient.baseUrl()}/api/uploads/image") {
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "image",
                            value = bytes,
                            headers = headers {
                                append("Content-Type", "image/jpeg")
                                append("Content-Disposition", "form-data; name=\"image\"; filename=\"$fileName\"")
                            }
                        )
                    }
                )
            )
        }

        val body: Map<String, String> = response.body()
        return body["url"] ?: throw Exception("Upload failed")
    }

    suspend fun createOutfit(
        userId: String,
        imageUrl: String,
        title: String,
        weatherCondition: String,
        season: String,
        storeName: String? = null,
        storeAddress: String? = null,
        price: String? = null,
        storePhone: String? = null,
        productUrl: String? = null
    ): OutfitResponse {
        return ApiClient.client.post("${ApiClient.baseUrl()}/api/outfits") {
            contentType(ContentType.Application.Json)
            setBody(
                CreateOutfitRequest(
                    userId = userId,
                    imageUrl = imageUrl,
                    title = title,
                    weatherCondition = weatherCondition,
                    season = season,
                    storeName = storeName,
                    storeAddress = storeAddress,
                    price = price,
                    storePhone = storePhone,
                    productUrl = productUrl
                )
            )
        }.body()
    }
}