package com.emirgasic.forecastfm.network.profile

import android.content.ContentResolver
import android.net.Uri
import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import kotlinx.serialization.Serializable
import java.io.InputStream

class ProfileApi {

    suspend fun getProfile(userId: String): ProfileResponse {
        return ApiClient.client.get(
            "${ApiClient.baseUrl()}/api/users/$userId/profile"
        ).body()
    }

    suspend fun updateProfile(userId: String, request: UpdateProfileRequest): Boolean {
        val response = ApiClient.client.put(
            "${ApiClient.baseUrl()}/api/users/$userId"
        ) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status.value in 200..299
    }

    suspend fun uploadProfileImage(
        userId: String,
        contentResolver: ContentResolver,
        imageUri: String
    ): String? {
        val uri = Uri.parse(imageUri)
        val inputStream: InputStream = contentResolver.openInputStream(uri)
            ?: return null

        val bytes = inputStream.use { it.readBytes() }
        val fileName = uri.lastPathSegment ?: "image.jpg"

        val response = ApiClient.client.post(
            "${ApiClient.baseUrl()}/api/users/$userId/profile-image"
        ) {
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

        return try {
            val body: Map<String, String> = response.body()
            body["url"]
        } catch (e: Exception) {
            null
        }
    }
}

