package com.emirgasic.forecastfm.feature.style.posts

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.NewPost
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.repository.NewPostRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.network.image.ImageUploadApi
import com.emirgasic.forecastfm.network.post.PostApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewPostViewModel(
    private val tokenManager: TokenManager,
    private val newPostRepository: NewPostRepository,
    private val postApi: PostApi,
    private val imageUploadApi: ImageUploadApi,
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    private val _newPost = MutableStateFlow<NewPost?>(null)
    val newPost: StateFlow<NewPost?> = _newPost.asStateFlow()

    private val _availableOutfits = MutableStateFlow<List<Outfit>>(emptyList())
    val availableOutfits: StateFlow<List<Outfit>> = _availableOutfits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadNewPost() {
        _newPost.value = newPostRepository.getNewPostData()
    }

    fun loadAvailableOutfits() {
        viewModelScope.launch {
            try {
                val userId = tokenManager.getUserId().first()
                if (userId == null) {
                    _availableOutfits.value = emptyList()
                    return@launch
                }
                _availableOutfits.value = outfitRepository
                    .getTrendingOutfits()
                    .filter { it.userId == userId }
            } catch (_: Exception) {
                _availableOutfits.value = emptyList()
            }
        }
    }

    /**
     * Called when AddOutfitScreen returns with a newly-created outfit ID.
     * Fetches the outfit and auto-selects it on the post being composed.
     */
    fun onOutfitCreated(outfitId: String) {
        viewModelScope.launch {
            try {
                val outfit = outfitRepository.getOutfitById(outfitId)
                _newPost.value = _newPost.value?.copy(
                    selectedOutfitId = outfit.id,
                    selectedOutfitTitle = outfit.title
                )
                loadAvailableOutfits()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to attach outfit"
            }
        }
    }

    fun selectOutfit(outfit: Outfit) {
        _newPost.value = _newPost.value?.copy(
            selectedOutfitId = outfit.id,
            selectedOutfitTitle = outfit.title
        )
    }

    fun clearOutfit() {
        _newPost.value = _newPost.value?.copy(
            selectedOutfitId = null,
            selectedOutfitTitle = null
        )
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

    fun createPost(
        uploadImage: suspend (postId: String, imageUri: Uri) -> Unit,
        onSuccess: () -> Unit
    ) {
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
                    imageUrl = null,
                    outfitId = post.selectedOutfitId
                )

                val imageUriString = post.image
                if (!imageUriString.isNullOrBlank()) {
                    try {
                        uploadImage(response.id, Uri.parse(imageUriString))
                    } catch (_: Exception) {
                    }
                }

                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.message ?: "Failed to create post"
            }
        }
    }
}