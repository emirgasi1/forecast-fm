package com.emirgasic.forecastfm.feature.settings.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.ProfileRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.location.LocationApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val username: String = "",
    val bio: String = "",
    val profileImageUrl: String = "",
    val favoriteLocation: String = "",
    val locations: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val savedMessage: String? = null,
    val error: String? = null
)

class EditProfileViewModel(
    private val tokenManager: TokenManager,
    private val profileRepository: ProfileRepository,
    private val userRepository: UserRepository,
    private val locationApi: LocationApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")
                currentUserId = userId

                val userResponse = userRepository.getUser(userId)
                    ?: throw Exception("User not found")

                _uiState.value = _uiState.value.copy(
                    username = userResponse.username,
                    bio = userResponse.bio ?: "",
                    profileImageUrl = userResponse.profileImageUrl ?: "",
                    favoriteLocation = userResponse.favoriteLocation ?: "",
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load profile"
                )
            }
        }
    }

    fun loadLocations() {
        viewModelScope.launch {
            try {
                val locations = locationApi.getLocations()
                _uiState.value = _uiState.value.copy(
                    locations = locations.map { it.name }
                )
            } catch (_: Exception) {
                // Silent failure — dropdown stays empty
            }
        }
    }

    fun updateUsername(value: String) {
        _uiState.value = _uiState.value.copy(username = value)
    }

    fun updateBio(value: String) {
        _uiState.value = _uiState.value.copy(bio = value)
    }

    fun updateLocation(value: String) {
        _uiState.value = _uiState.value.copy(favoriteLocation = value)
    }

    /**
     * Uploads a profile image.
     *
     * @param uploadImage a suspend lambda that uploads the image for the given
     *                    userId and returns the URL, or null on failure.
     *                    Implemented by the UI layer so this ViewModel stays
     *                    free of android.content.ContentResolver.
     */
    fun uploadImage(
        imageUri: String,
        uploadImage: suspend (userId: String, imageUri: String) -> String?
    ) {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                _uiState.value = _uiState.value.copy(isSaving = true, error = null)

                val uploadedUrl = uploadImage(userId, imageUri)

                if (uploadedUrl != null) {
                    _uiState.value = _uiState.value.copy(
                        profileImageUrl = uploadedUrl,
                        isSaving = false,
                        savedMessage = "Image uploaded"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Image upload failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Image upload failed"
                )
            }
        }
    }

    fun saveProfile() {
        viewModelScope.launch {
            try {
                val userId = currentUserId ?: return@launch
                _uiState.value = _uiState.value.copy(
                    isSaving = true,
                    error = null,
                    savedMessage = null
                )

                val success = profileRepository.updateProfile(
                    userId = userId,
                    username = _uiState.value.username,
                    bio = _uiState.value.bio,
                    favoriteLocation = _uiState.value.favoriteLocation
                )

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    savedMessage = if (success) "Profile saved" else null,
                    error = if (success) null else "Failed to save profile"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save profile"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(savedMessage = null, error = null)
    }
}