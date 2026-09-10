package com.emirgasic.forecastfm.feature.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.music.MusicPlaylistCard
import com.emirgasic.forecastfm.core.ui.components.music.formatPlaylistDuration

@Composable
fun SavedPlaylistsScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: SavedPlaylistsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SavedPlaylistsViewModel(tokenManager) as T
            }
        }
    )
) {
    val savedPlaylists by viewModel.savedPlaylists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favoritePlaylistIds by viewModel.favoritePlaylistIds.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "← Back",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                }

                item {
                    Text(
                        text = "Saved Playlists",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                if (savedPlaylists.isEmpty()) {
                    item {
                        Text(
                            text = "No saved playlists yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                } else {
                    items(savedPlaylists) { playlist ->
                        MusicPlaylistCard(
                            title = playlist.title,
                            genre = playlist.genre,
                            firstSong = playlist.songs.firstOrNull()?.title ?: "Unknown",
                            songs = "${playlist.songs.size} songs",
                            duration = formatPlaylistDuration(playlist.songs),
                            likes = playlist.likes.toString(),
                            isFavorite = playlist.id in favoritePlaylistIds,
                            onFavoriteClick = {
                                viewModel.unsavePlaylist(playlist.id)
                            },
                            onClick = {
                                navController.navigate(
                                    Routes.playlistRoute(playlist.id)
                                )
                            },
                            onPlayClick = {
                                viewModel.openPlaylist(playlist)
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}