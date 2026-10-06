package com.emirgasic.forecastfm.feature.music.playlist

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.music.playlist.ExternalMusicLinkCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.MusicRow
import com.emirgasic.forecastfm.core.ui.components.music.playlist.PlaylistHeaderCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.PlaylistTagCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.SimilarPlaylistCard
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.feature.weather.WeatherViewModel
import com.emirgasic.forecastfm.network.playlist.PlaylistApi
import com.emirgasic.forecastfm.network.weather.WeatherApi
import com.emirgasic.forecastfm.network.youtube.YouTubeApi
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors
import com.emirgasic.forecastfm.utils.TagIconMapper

@Composable
fun PlaylistScreen(
    navController: NavController,
    playlistId: String?,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val forecastColors = LocalForecastColors.current

    val viewModel: PlaylistViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                val playlistApi = PlaylistApi()
                return PlaylistViewModel(
                    tokenManager = tokenManager,
                    playlistRepository = PlaylistRepository(playlistApi),
                    youTubeApi = YouTubeApi()
                ) as T
            }
        }
    )

    val weatherViewModel: WeatherViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WeatherViewModel(
                    repository = WeatherRepository(WeatherApi())
                ) as T
            }
        }
    )

    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val similarPlaylists by viewModel.similarPlaylists.collectAsState()
    val weather by weatherViewModel.weather.collectAsState()

    val locationRepository = remember { LocationRepository() }

    LaunchedEffect(playlistId) {
        playlistId?.let {
            viewModel.loadPlaylist(it)
        }
    }

    when (val state = uiState) {

        PlaylistUiState.Loading -> {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                LoadingScreen()
            }
        }

        is PlaylistUiState.Error -> {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = state.message,
                        color = forecastColors.body,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = {
                            playlistId?.let {
                                viewModel.loadPlaylist(it)
                            }
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }
        }

        is PlaylistUiState.Success -> {
            val playlist = state.playlist

            LaunchedEffect(playlist.location) {
                try {
                    val locations = locationRepository.getLocations()

                    var location = locations.firstOrNull { it.name == playlist.location }

                    if (location == null) {
                        location = locations.firstOrNull {
                            it.name.equals(playlist.location, ignoreCase = true)
                        }
                    }

                    if (location == null) {
                        location = locations.firstOrNull {
                            it.name.contains(playlist.location, ignoreCase = true) ||
                                    playlist.location.contains(it.name, ignoreCase = true)
                        }
                    }

                    if (location == null) {
                        location = locations.firstOrNull()
                    }

                    location?.let {
                        weatherViewModel.loadWeather(
                            location = it.name,
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                    }
                } catch (_: Exception) {
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 60.dp, start = 10.dp, end = 10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {

                    item {
                        PlaylistHeaderCard(
                            album = playlist.albumImageUrl,
                            title = playlist.title,
                            genre = playlist.genre,
                            mood = playlist.mood,
                            weatherIcon = painterResource(weather?.icon ?: R.drawable.sun),
                            weather = weather?.condition ?: playlist.weather,
                            temperature = weather?.temperature ?: playlist.temperature,
                            locationIcon = painterResource(R.drawable.mappin),
                            location = playlist.location,
                            isFavorite = isFavorite,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(playlist.id)
                            }
                        )
                    }

                    item {
                        SectionTitle(title = "Songs")
                    }

                    items(playlist.songs.take(3)) { song ->
                        MusicRow(
                            title = song.title,
                            artist = song.artist,
                            duration = song.duration,
                            image = song.albumImageUrl,
                            onClick = {
                                val url = "https://www.youtube.com/watch?v=${song.id}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }

                    if (playlist.bestFor.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            SectionTitle(title = "Best For")
                        }

                        items(playlist.bestFor) { tag ->
                            PlaylistTagCard(
                                icon = painterResource(TagIconMapper.getIconForTag(tag)),
                                title = tag
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        SectionTitle(title = "Would You Rather")
                    }

                    item {
                        ExternalMusicLinkCard(
                            icon = painterResource(R.drawable.music),
                            title = "Open in Spotify",
                            onClick = {
                                playlist.spotifyUrl?.let { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            }
                        )
                    }

                    item {
                        ExternalMusicLinkCard(
                            icon = painterResource(R.drawable.play),
                            title = "Open in YouTube",
                            onClick = {
                                playlist.youtubeUrl?.let { url ->
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            }
                        )
                    }

                    if (similarPlaylists.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            SectionTitle(title = "Similar Playlists")
                        }

                        items(similarPlaylists) { similarPlaylist ->
                            SimilarPlaylistCard(
                                albumUrl = similarPlaylist.albumImageUrl,
                                title = similarPlaylist.title,
                                genre = similarPlaylist.genre,
                                mood = similarPlaylist.mood,
                                locationIcon = painterResource(R.drawable.mappin),
                                location = similarPlaylist.location,
                                onClick = {
                                    navController.navigate(
                                        Routes.playlistRoute(similarPlaylist.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}