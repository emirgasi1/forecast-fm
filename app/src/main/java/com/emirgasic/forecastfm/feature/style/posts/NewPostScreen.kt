package com.emirgasic.forecastfm.feature.style.posts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.style.posts.DropdownSelector
import com.emirgasic.forecastfm.core.ui.components.style.posts.ImagePickerCard
import com.emirgasic.forecastfm.core.ui.components.style.posts.PostActionButtons
import com.emirgasic.forecastfm.core.ui.components.style.posts.ProfileInputField
import com.emirgasic.forecastfm.core.utils.rememberImagePicker
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.repository.NewPostRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.network.image.ImageUploadApi
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

private const val KEY_NEW_OUTFIT_ID = "newOutfitId"

@Composable
fun NewPostScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: NewPostViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NewPostViewModel(
                    tokenManager = tokenManager,
                    newPostRepository = NewPostRepository(),
                    postApi = PostApi(),
                    imageUploadApi = ImageUploadApi(),
                    outfitRepository = OutfitRepository()
                ) as T
            }
        }
    )
) {
    val newPost by viewModel.newPost.collectAsState()
    val availableOutfits by viewModel.availableOutfits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Result channel from AddOutfitScreen
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val newOutfitIdFlow = remember(savedStateHandle) {
        savedStateHandle?.getStateFlow<String?>(KEY_NEW_OUTFIT_ID, null)
    }
    val newOutfitId by (newOutfitIdFlow
        ?: kotlinx.coroutines.flow.MutableStateFlow<String?>(null)).collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadNewPost()
        viewModel.loadAvailableOutfits()
    }

    LaunchedEffect(newOutfitId) {
        newOutfitId?.let { id ->
            viewModel.onOutfitCreated(id)
            savedStateHandle?.remove<String?>(KEY_NEW_OUTFIT_ID)
        }
    }

    val post = newPost ?: return

    val imagePicker = rememberImagePicker { uri ->
        uri?.let {
            viewModel.updateImage(it.toString())
        }
    }
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val imageUploadApi = remember { ImageUploadApi() }
    val forecastColors = LocalForecastColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, end = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Text(
                    text = "New Post",
                    color = forecastColors.title,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage ?: "",
                        color = forecastColors.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                ImagePickerCard(
                    image = post.image,
                    icon = painterResource(R.drawable.clothes),
                    text = if (post.image != null) "Change photo" else "Add a photo",
                    onClick = {
                        imagePicker.pickFromGallery()
                    }
                )
            }

            item {
                OutfitPickerRow(
                    outfits = availableOutfits,
                    selectedOutfitId = post.selectedOutfitId,
                    onSelect = { viewModel.selectOutfit(it) },
                    onClear = { viewModel.clearOutfit() },
                    onCreateNew = {
                        navController.navigate(Routes.AddOutfit)
                    }
                )
            }

            item {
                ProfileInputField(
                    title = "Caption",
                    value = post.caption,
                    placeholder = "What's today's vibe?",
                    onValueChange = {
                        viewModel.updateCaption(it)
                    }
                )
            }

            item {
                ProfileInputField(
                    title = "Weather",
                    value = post.weather,
                    placeholder = "Sunny",
                    onValueChange = {
                        viewModel.updateWeather(it)
                    }
                )
            }

            item {
                ProfileInputField(
                    title = "Location",
                    value = post.location,
                    placeholder = "Baščaršija",
                    onValueChange = {
                        viewModel.updateLocation(it)
                    }
                )
            }

            item {
                DropdownSelector(
                    title = "Playlist",
                    selected = post.selectedPlaylist,
                    options = post.playlists,
                    onSelected = {
                        viewModel.selectPlaylist(it)
                    }
                )
            }

            item {
                PostActionButtons(
                    onPostClick = {
                        viewModel.createPost(
                            uploadImage = { postId, imageUri ->
                                imageUploadApi.uploadImage(
                                    postId,
                                    contentResolver,
                                    imageUri.toString()
                                )
                            },
                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    },
                    onDeleteClick = {
                        navController.popBackStack()
                    },
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
private fun OutfitPickerRow(
    outfits: List<Outfit>,
    selectedOutfitId: String?,
    onSelect: (Outfit) -> Unit,
    onClear: () -> Unit,
    onCreateNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    val forecastColors = LocalForecastColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Outfit (optional)",
            color = forecastColors.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
        ) {
            items(outfits, key = { it.id }) { outfit ->
                OutfitPickerCard(
                    outfit = outfit,
                    isSelected = outfit.id == selectedOutfitId,
                    onClick = {
                        if (outfit.id == selectedOutfitId) onClear() else onSelect(outfit)
                    }
                )
            }

            item {
                CreateNewOutfitCard(onClick = onCreateNew)
            }
        }
    }
}

@Composable
private fun OutfitPickerCard(
    outfit: Outfit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val forecastColors = LocalForecastColors.current
    val borderColor = if (isSelected) forecastColors.primary else forecastColors.border
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = outfit.imageUrl,
                contentDescription = outfit.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = outfit.title,
            color = forecastColors.body,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier
                .padding(top = 4.dp)
                .padding(horizontal = 2.dp)
        )
    }
}

@Composable
private fun CreateNewOutfitCard(onClick: () -> Unit) {
    val forecastColors = LocalForecastColors.current

    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, forecastColors.border, RoundedCornerShape(12.dp))
                .background(forecastColors.card),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "＋",
                color = forecastColors.primary,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Text(
            text = "Create new",
            color = forecastColors.muted,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier
                .padding(top = 4.dp)
                .padding(horizontal = 2.dp)
        )
    }
}