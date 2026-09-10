package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.post.PostResponse

class SavedPostRepository(
    private val postApi: PostApi = PostApi()
) {

    suspend fun savePost(postId: String, userId: String) {
        postApi.savePost(postId, userId)
    }

    suspend fun unsavePost(postId: String, userId: String) {
        postApi.unsavePost(postId, userId)
    }

    suspend fun isPostSaved(postId: String, userId: String): Boolean {
        return postApi.isPostSaved(postId, userId)
    }

    suspend fun getSavedPosts(userId: String): List<PostResponse> {
        return postApi.getSavedPosts(userId)
    }
}