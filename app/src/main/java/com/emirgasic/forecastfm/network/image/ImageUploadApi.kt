package com.emirgasic.forecastfm.network.image

import android.content.ContentResolver
import android.net.Uri
import com.emirgasic.forecastfm.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.headers
import java.io.InputStream

class ImageUploadApi {

    suspend fun uploadImage(postId: String, contentResolver: ContentResolver, imageUri: String): String {
        val uri = Uri.parse(imageUri)
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            throw Exception("Failed to open image stream")
        }

        val bytes = inputStream.use { it.readBytes() }
        val fileName = uri.lastPathSegment ?: "image.jpg"

        val response = ApiClient.client.post("${ApiClient.baseUrl()}/api/posts/$postId/image") {
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

        return response.body()
    }
}