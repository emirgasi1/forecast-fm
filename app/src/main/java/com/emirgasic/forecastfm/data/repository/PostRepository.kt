package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.post.PostResponse

class PostRepository(
    private val postApi: PostApi
) {

    suspend fun getPosts(): List<PostResponse> {
        return postApi.getPosts()
    }
}