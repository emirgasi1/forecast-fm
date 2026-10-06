package com.emirgasic.forecastfm.feature.feed.postdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.utils.formatIsoDateTime
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun PostDetailScreen(
    navController: NavController,
    postId: String?,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PostDetailViewModel(
                    postApi = PostApi()
                ) as T
            }
        }
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val forecastColors = LocalForecastColors.current

    LaunchedEffect(postId) {
        postId?.let { viewModel.loadPost(it) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        when (val state = uiState) {

            PostDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingScreen()
                }
            }

            is PostDetailUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Couldn't load post",
                        color = forecastColors.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        color = forecastColors.muted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { postId?.let { viewModel.loadPost(it) } }) {
                        Text("Retry")
                    }
                }
            }

            is PostDetailUiState.Success -> {
                val post = state.post
                val imageUrl = viewModel.resolveImageUrl(post.imageUrl)

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {

                    item {
                        Text(
                            text = "Back",
                            color = forecastColors.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 20.dp)
                                .clickable {
                                    navController.popBackStack()
                                }
                        )
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(4f / 5f)
                                .clip(MaterialTheme.shapes.medium)
                                .padding(horizontal = 16.dp)
                        ) {
                            if (imageUrl != null) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = post.caption,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(forecastColors.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No image",
                                        color = forecastColors.muted,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }

                    if (!post.outfitTitle.isNullOrBlank()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "👕 ${post.outfitTitle}",
                                color = forecastColors.muted,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }

                    if (!post.caption.isNullOrBlank()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = post.caption,
                                color = forecastColors.body,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = formatIsoDateTime(post.createdAt),
                            color = forecastColors.muted,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }
        }
    }
}