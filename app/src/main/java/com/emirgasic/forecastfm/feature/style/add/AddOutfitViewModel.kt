package com.emirgasic.forecastfm.feature.style.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.network.outfit.OutfitApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AddOutfitState(
    val imageUri: Uri? = null,
    val title: String = "",
    val weatherCondition: String = "",
    val season: String = "",
    val storeName: String = "",
    val storeAddress: String = "",
    val price: String = "",
    val storePhone: String = "",
    val productUrl: String = "",
    val isUploading: Boolean = false,
    val error: String? = null
) {
    val canSubmit: Boolean
        get() = imageUri != null &&
                title.isNotBlank() &&
                weatherCondition.isNotBlank() &&
                season.isNotBlank()
}

class AddOutfitViewModel(
    private val tokenManager: TokenManager,
    private val outfitApi: OutfitApi
) : ViewModel() {

    private val _state = MutableStateFlow(AddOutfitState())
    val state: StateFlow<AddOutfitState> = _state.asStateFlow()

    fun setImage(uri: Uri) {
        _state.value = _state.value.copy(imageUri = uri, error = null)
    }

    fun setTitle(value: String) {
        _state.value = _state.value.copy(title = value, error = null)
    }

    fun setWeatherCondition(value: String) {
        _state.value = _state.value.copy(weatherCondition = value, error = null)
    }

    fun setSeason(value: String) {
        _state.value = _state.value.copy(season = value, error = null)
    }

    fun setStoreName(value: String) {
        _state.value = _state.value.copy(storeName = value)
    }

    fun setStoreAddress(value: String) {
        _state.value = _state.value.copy(storeAddress = value)
    }

    fun setPrice(value: String) {
        _state.value = _state.value.copy(price = value)
    }

    fun setStorePhone(value: String) {
        _state.value = _state.value.copy(storePhone = value)
    }

    fun setProductUrl(value: String) {
        _state.value = _state.value.copy(productUrl = value)
    }


    fun submit(
        uploadImage: suspend (Uri) -> String,
        onSuccess: () -> Unit
    ) {
        val s = _state.value
        val imageUri = s.imageUri ?: return

        viewModelScope.launch {
            _state.value = s.copy(isUploading = true, error = null)

            try {
                val userId = tokenManager.getUserId().first()
                    ?: throw Exception("User not logged in")

                val uploadedUrl = uploadImage(imageUri)

                outfitApi.createOutfit(
                    userId = userId,
                    imageUrl = uploadedUrl,
                    title = s.title.trim(),
                    weatherCondition = s.weatherCondition,
                    season = s.season,
                    storeName = s.storeName.ifBlank { null },
                    storeAddress = s.storeAddress.ifBlank { null },
                    price = s.price.ifBlank { null },
                    storePhone = s.storePhone.ifBlank { null },
                    productUrl = s.productUrl.ifBlank { null }
                )

                _state.value = _state.value.copy(isUploading = false)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isUploading = false,
                    error = e.message ?: "Failed to add outfit"
                )
            }
        }
    }
}