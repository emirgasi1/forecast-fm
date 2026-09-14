package com.emirgasic.forecastfm.feature.music.playlist

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.music.playlist.ExternalMusicLinkCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.MusicRow
import com.emirgasic.forecastfm.core.ui.components.music.playlist.PlaylistHeaderCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.PlaylistTagCard
import com.emirgasic.forecastfm.core.ui.components.music.playlist.SimilarPlaylistCard
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.feature.weather.WeatherViewModel
import com.emirgasic.forecastfm.utils.TagIconMapper

@Composable
fun PlaylistScreen(
    navController: NavController,
    playlistId: String?,
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val context = LocalContext.current
    val viewModel: PlaylistViewModel = viewModel()

    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val similarPlaylists by viewModel.similarPlaylists.collectAsState()
    val weather by weatherViewModel.weather.collectAsState()

    val locationRepository = LocationRepository()

    LaunchedEffect(playlistId) {
        playlistId?.let {
            viewModel.loadPlaylist(it)
        }
    }

    when (val state = uiState) {

        PlaylistUiState.Loading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is PlaylistUiState.Error -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = state.message,
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .padding(top = 60.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier.height(28.dp))
                    }

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
                        Spacer(modifier.height(28.dp))
                    }

                    item {
                        SectionTitle(title = "Songs")
                    }

                    item {
                        Spacer(modifier.height(12.dp))
                    }

                    items(playlist.songs.take(3)) { song ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val url = "https://www.youtube.com/watch?v=${song.id}"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                        ) {
                            MusicRow(
                                title = song.title,
                                artist = song.artist,
                                duration = song.duration,
                                image = song.albumImageUrl
                            )
                        }
                        Spacer(modifier.height(12.dp))
                    }

                    item {
                        Spacer(modifier.height(28.dp))
                    }

                    if (playlist.bestFor.isNotEmpty()) {
                        item {
                            SectionTitle(title = "Best For")
                        }

                        item {
                            Spacer(modifier.height(12.dp))
                        }

                        items(playlist.bestFor) { tag ->
                            PlaylistTagCard(
                                icon = painterResource(TagIconMapper.getIconForTag(tag)),
                                title = tag
                            )
                            Spacer(modifier.height(12.dp))
                        }
                    }

                    item {
                        Spacer(modifier.height(28.dp))
                    }

                    item {
                        SectionTitle(title = "Would You Rather")
                    }

                    item {
                        Spacer(modifier.height(12.dp))
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
                        Spacer(modifier.height(12.dp))
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

                    item {
                        Spacer(modifier.height(28.dp))
                    }

                    if (similarPlaylists.isNotEmpty()) {
                        item {
                            SectionTitle(title = "Similar Playlists")
                        }

                        item {
                            Spacer(modifier.height(16.dp))
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
                            Spacer(modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}