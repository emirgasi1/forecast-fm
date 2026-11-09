package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.local.daos.saved.SavedPostDao
import com.emirgasic.forecastfm.data.local.entities.saved.SavedPostEntity
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.post.PostResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavedPostRepository(
    private val savedPostDao: SavedPostDao,
    private val postApi: PostApi = PostApi()
) {

    fun getSavedPostIds(): Flow<List<String>> {
        return savedPostDao.getSavedPosts().map { list -> list.map { it.postId } }
    }

    fun isPostSavedLocally(postId: String): Flow<Boolean> {
        return savedPostDao.isPostSaved(postId)
    }

    suspend fun toggleSavePost(postId: String, userId: String, isCurrentlySaved: Boolean) {
        if (isCurrentlySaved) {
            savedPostDao.unsavePost(postId)
            try { postApi.unsavePost(postId, userId) } catch (_: Exception) {}
        } else {
            savedPostDao.savePost(SavedPostEntity(postId))
            try { postApi.savePost(postId, userId) } catch (_: Exception) {}
        }
    }

    // Fetch the full post details from the network based on the saved IDs
    suspend fun getSavedPostDetails(userId: String): List<PostResponse> {
        return try {
            postApi.getSavedPosts(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }
}