package com.emirgasic.forecastfm.feature.music

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.common.SearchField
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.common.WeatherRecommendationHeader
import com.emirgasic.forecastfm.core.ui.components.music.CompactPlaylistCard
import com.emirgasic.forecastfm.core.ui.components.music.MusicHistoryCard
import com.emirgasic.forecastfm.core.ui.components.music.PlaylistCarousel
import com.emirgasic.forecastfm.core.ui.components.music.RecommendedMusicCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.MusicRow
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun MusicScreen(
    mainNavController: NavController,
    rootNavController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val forecastColors = LocalForecastColors.current

    val viewModel: MusicViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MusicViewModel(
                    tokenManager,
                    OnboardingPreferences(context)
                ) as T
            }
        }
    )

    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val search by viewModel.search.collectAsState()
    val weather by viewModel.weather.collectAsState()
    val weatherPlaylists by viewModel.weatherPlaylists.collectAsState()
    val trendingPlaylists by viewModel.trendingPlaylists.collectAsState()
    val favoritePlaylistIds by viewModel.favoritePlaylistIds.collectAsState()
    val recommendedPlaylist by viewModel.recommendedPlaylist.collectAsState()
    val musicHistory by viewModel.musicHistory.collectAsState()
    val lastPlayed = musicHistory.firstOrNull()?.title ?: "No history"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ScreenTitle(
                icon = painterResource(R.drawable.music),
                title = "Music"
            )

            Spacer(modifier = Modifier.height(24.dp))

            SearchField(
                value = search,
                onValueChange = { viewModel.updateSearch(it) },
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
                            color = forecastColors.border,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(forecastColors.card)
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingScreen()
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
                            color = forecastColors.muted,
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
                                HorizontalDivider(color = forecastColors.border.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {

                    item {
                        Spacer(modifier = Modifier.height(12.dp))

                        WeatherRecommendationHeader(
                            title = "Today's Vibe",
                            subtitle = weather?.let {
                                "${it.condition}, ${it.temperature} - Discover new music"
                            } ?: "Loading weather...",
                            icon = painterResource(weather?.icon ?: R.drawable.sun)
                        )
                    }

                    if (weatherPlaylists.isNotEmpty()) {
                        item {
                            PlaylistCarousel(
                                playlists = weatherPlaylists,
                                favoritePlaylistIds = favoritePlaylistIds,
                                onFavoriteClick = { viewModel.toggleFavorite(it) },
                                onPlayClick = { playlist ->
                                    viewModel.openPlaylist(playlist)
                                    playlist.youtubeUrl?.let { url ->
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    }
                                },
                                onClick = { playlist ->
                                    rootNavController.navigate(Routes.playlistRoute(playlist.id))
                                }
                            )
                        }
                    }

                    if (trendingPlaylists.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))

                            SectionTitle(
                                title = "Trending",
                                icon = painterResource(R.drawable.fire)
                            )
                        }

                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(trendingPlaylists) { playlist ->
                                    CompactPlaylistCard(
                                        title = playlist.title,
                                        genre = playlist.genre,
                                        image = playlist.albumImageUrl,
                                        onClick = {
                                            rootNavController.navigate(Routes.playlistRoute(playlist.id))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        SectionTitle(
                            title = "Music History",
                            icon = painterResource(R.drawable.music)
                        )
                    }

                    item {
                        MusicHistoryCard(
                            lastPlayed = lastPlayed,
                            onClick = {
                                rootNavController.navigate(Routes.MusicHistory)
                            }
                        )
                    }

                    recommendedPlaylist?.let { playlist ->
                        item {
                            SectionTitle(
                                title = "Recommended For You",
                                icon = painterResource(R.drawable.stars)
                            )
                        }

                        item {
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
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    }
                                },
                                onViewPlaylistClick = { id ->
                                    rootNavController.navigate(Routes.playlistRoute(id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}