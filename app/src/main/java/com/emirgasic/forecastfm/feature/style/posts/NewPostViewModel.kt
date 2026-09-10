package com.emirgasic.forecastfm.feature.style.posts

import android.content.ContentResolver
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.NewPost
import com.emirgasic.forecastfm.data.repository.NewPostRepository
import com.emirgasic.forecastfm.network.image.ImageUploadApi
import com.emirgasic.forecastfm.network.post.PostApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewPostViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val repository = NewPostRepository()
    private val postApi = PostApi()
    private val imageUploadApi = ImageUploadApi()

    private val _newPost = MutableStateFlow<NewPost?>(null)
    val newPost: StateFlow<NewPost?> = _newPost.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadNewPost()
    }

    private fun loadNewPost() {
        _newPost.value = repository.getNewPostData()
    }

    fun updateImage(image: String?) {
        _newPost.value = _newPost.value?.copy(image = image)
    }

    fun updateCaption(value: String) {
        _newPost.value = _newPost.value?.copy(caption = value)
    }

    fun updateWeather(value: String) {
        _newPost.value = _newPost.value?.copy(weather = value)
    }

    fun updateLocation(value: String) {
        _newPost.value = _newPost.value?.copy(location = value)
    }

    fun selectPlaylist(value: String) {
        _newPost.value = _newPost.value?.copy(selectedPlaylist = value)
    }

    fun createPost(onSuccess: () -> Unit, contentResolver: ContentResolver) {
        val post = _newPost.value ?: return

        if (post.caption.isBlank()) {
            _errorMessage.value = "Please add a caption"
            return
        }

        if (post.location.isBlank()) {
            _errorMessage.value = "Please add a location"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val response = postApi.createPost(
                    userId = userId,
                    caption = post.caption,
                    imageUrl = null
                )

                Log.d("NewPost", "Post created: ${response.id}")

                post.image?.let { imageUri ->
                    try {
                        imageUploadApi.uploadImage(response.id, contentResolver, imageUri)
                        Log.d("NewPost", "Image uploaded successfully")
                    } catch (e: Exception) {
                        Log.e("NewPost", "Image upload failed: ${e.message}")
                    }
                }

                _isLoading.value = false
                onSuccess()

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message ?: "Failed to create post"
                Log.e("NewPost", "Error: ${e.message}", e)
            }
        }
    }
}