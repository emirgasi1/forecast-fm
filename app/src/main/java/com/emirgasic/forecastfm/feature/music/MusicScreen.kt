package com.emirgasic.forecastfm.feature.music

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.common.SearchField
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.common.WeatherRecommendationHeader
import com.emirgasic.forecastfm.core.ui.components.music.MusicHistoryCard
import com.emirgasic.forecastfm.core.ui.components.music.MusicPlaylistCard
import com.emirgasic.forecastfm.core.ui.components.music.RecommendedMusicCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.MusicRow

@Composable
fun MusicScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: MusicViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MusicViewModel(tokenManager) as T
            }
        }
    )
) {
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val search by viewModel.search.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val weatherPlaylists by viewModel.weatherPlaylists.collectAsState()
    val trendingPlaylists by viewModel.trendingPlaylists.collectAsState()
    val favoritePlaylistIds by viewModel.favoritePlaylistIds.collectAsState()
    val recommendedPlaylist by viewModel.recommendedPlaylist.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val musicHistory by viewModel.musicHistory.collectAsState()
    val lastPlayed = musicHistory.firstOrNull()?.title ?: "No history"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        ScreenTitle(
            icon = painterResource(R.drawable.music),
            title = "Music"
        )

        Spacer(modifier = Modifier.height(20.dp))

        SearchField(
            value = search,
            onValueChange = {
                viewModel.updateSearch(it)
            },
            placeholder = "Search songs..."
        )

        if (search.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .zIndex(10f)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (tracks.isEmpty()) {
                    Text(
                        text = "No results found for '$search'",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                    ) {
                        items(tracks) { video ->
                            MusicRow(
                                title = video.snippet?.title ?: "Unknown",
                                artist = video.snippet?.channelTitle ?: "Unknown Artist",
                                duration = 0,
                                image = video.snippet?.thumbnails?.high?.url
                                    ?: video.snippet?.thumbnails?.medium?.url
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        if (search.isBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                WeatherRecommendationHeader(
                    title = "Today's Vibe",
                    subtitle = weather?.let {
                        "${it.condition}, ${it.temperature} - Discover new music"
                    } ?: "Loading weather...",
                    icon = painterResource(weather?.icon ?: R.drawable.sun)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (weatherPlaylists.isNotEmpty()) {
                    val weatherMatchPlaylist = weatherPlaylists.firstOrNull()
                    weatherMatchPlaylist?.let { playlist ->
                        MusicPlaylistCard(
                            title = playlist.title,
                            genre = playlist.genre,
                            mood = playlist.mood,
                            weather = weather?.condition ?: playlist.weather,
                            temperature = weather?.temperature ?: playlist.temperature,
                            location = playlist.location,
                            likes = playlist.likes.toString(),
                            isFavorite = playlist.id in favoritePlaylistIds,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(playlist.id)
                            },
                            onClick = {
                                rootNavController.navigate(
                                    Routes.playlistRoute(playlist.id)
                                )
                            },
                            onPlayClick = {
                                viewModel.openPlaylist(playlist)
                                playlist.youtubeUrl?.let { url ->
                                    context.startActivity(
                                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle(
                    title = "Weather Playlists",
                    icon = painterResource(R.drawable.music)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val weatherPlaylistItems = weatherPlaylists.take(2)
                weatherPlaylistItems.forEach { playlist ->
                    MusicPlaylistCard(
                        title = playlist.title,
                        genre = playlist.genre,
                        mood = playlist.mood,
                        weather = weather?.condition ?: playlist.weather,
                        temperature = weather?.temperature ?: playlist.temperature,
                        location = playlist.location,
                        likes = playlist.likes.toString(),
                        isFavorite = playlist.id in favoritePlaylistIds,
                        onFavoriteClick = {
                            viewModel.toggleFavorite(playlist.id)
                        },
                        onClick = {
                            rootNavController.navigate(
                                Routes.playlistRoute(playlist.id)
                            )
                        },
                        onPlayClick = {
                            viewModel.openPlaylist(playlist)
                            playlist.youtubeUrl?.let { url ->
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle(
                    title = "Trending",
                    icon = painterResource(R.drawable.fire)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val trendingItems = trendingPlaylists.take(2)
                trendingItems.forEach { playlist ->
                    MusicPlaylistCard(
                        title = playlist.title,
                        genre = playlist.genre,
                        mood = playlist.mood,
                        weather = weather?.condition ?: playlist.weather,
                        temperature = weather?.temperature ?: playlist.temperature,
                        location = playlist.location,
                        likes = playlist.likes.toString(),
                        isFavorite = playlist.id in favoritePlaylistIds,
                        onFavoriteClick = {
                            viewModel.toggleFavorite(playlist.id)
                        },
                        onClick = {
                            rootNavController.navigate(
                                Routes.playlistRoute(playlist.id)
                            )
                        },
                        onPlayClick = {
                            viewModel.openPlaylist(playlist)
                            playlist.youtubeUrl?.let { url ->
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle(
                    title = "Music History",
                    icon = painterResource(R.drawable.music)
                )

                Spacer(modifier = Modifier.height(16.dp))

                MusicHistoryCard(
                    lastPlayed = lastPlayed,
                    onClick = {
                        rootNavController.navigate(Routes.MusicHistory)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitle(
                    title = "Recommended For You",
                    icon = painterResource(R.drawable.stars)
                )

                Spacer(modifier = Modifier.height(16.dp))

                recommendedPlaylist?.let { playlist ->
                    RecommendedMusicCard(
                        id = playlist.id,
                        image = playlist.albumImageUrl,
                        title = playlist.title,
                        genre = playlist.genre,
                        mood = playlist.mood,
                        likes = playlist.likes.toString(),
                        onPlayClick = {
                            viewModel.openPlaylist(playlist)
                            playlist.youtubeUrl?.let { url ->
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                )
                            }
                        },
                        onViewPlaylistClick = { id ->
                            rootNavController.navigate(
                                Routes.playlistRoute(id)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}