package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.network.like.LikeApi

class LikeRepository(
    private val likeApi: LikeApi = LikeApi()
) {
    suspend fun likePost(postId: String, userId: String) {
        likeApi.likePost(postId, userId)
    }

    suspend fun unlikePost(postId: String, userId: String) {
        likeApi.unlikePost(postId, userId)
    }

    suspend fun isPostLiked(postId: String, userId: String): Boolean {
        return likeApi.isPostLiked(postId, userId)
    }

    suspend fun getLikeCount(postId: String): Int {
        return likeApi.getLikeCount(postId)
    }
}