package com.emirgasic.forecastfm.feature.music

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emirgasic.forecastfm.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.emirgasic.forecastfm.core.navigation.Routes
import com.emirgasic.forecastfm.core.ui.components.music.CategoryFilterRow
import com.emirgasic.forecastfm.core.ui.components.music.MusicHistoryCard
import com.emirgasic.forecastfm.core.ui.components.music.MusicPlaylistCard
import com.emirgasic.forecastfm.core.ui.components.music.RecommendedMusicCard
import com.emirgasic.forecastfm.core.ui.components.common.ScreenTitle
import com.emirgasic.forecastfm.core.ui.components.common.SearchField
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.common.WeatherRecommendationHeader
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.emirgasic.forecastfm.feature.music.MusicViewModel
@Composable
fun MusicScreen(mainNavController: NavController,
                rootNavController: NavController,
                modifier: Modifier =Modifier,
                viewModel: MusicViewModel = viewModel()) {
    val playlists by viewModel.playlists.collectAsState()

    val weatherPlaylists by viewModel.weatherPlaylists.collectAsState()

    val trendingPlaylists by viewModel.trendingPlaylists.collectAsState()

    val recommendedPlaylist = playlists.firstOrNull()

    var search by remember {
        mutableStateOf("")
    }
    var selectedGenre by remember{
        mutableStateOf("")
    }
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, start = 10.dp, bottom = 10.dp, end = 10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            item {
                ScreenTitle(
                    icon = painterResource(R.drawable.music),
                    title = "Music"
                )
            }
            item{
            Spacer(modifier = Modifier.height(20.dp))
            }
            item {
                SearchField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = "search playlists..."
                )
            }
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
            item {
                WeatherRecommendationHeader(
                    title = "For Today's Weather",
                    subtitle = "Because it's Sunny and 24°C",
                    icon = painterResource(R.drawable.sun)
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            items(weatherPlaylists){ playlist ->

                MusicPlaylistCard(
                    title = playlist.title,
                    genre = playlist.genre,
                    firstSong = playlist.songs.firstOrNull()?.title ?: "Unknown",
                    songs = "${playlist.songs.size} songs",
                    duration = "1h 12min",
                    likes = playlist.likes.toString()
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
            item {
                Spacer(modifier.height(16.dp))
            }
            items(trendingPlaylists){ playlist ->

                MusicPlaylistCard(
                    title = playlist.title,
                    genre = playlist.genre,
                    firstSong = playlist.songs.firstOrNull()?.title ?: "Unknown",
                    songs = "${playlist.songs.size} songs",
                    duration = "1h 12min",
                    likes = playlist.likes.toString()
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            item {
                Spacer(modifier.height(36.dp))
            }

            item {
                SectionTitle(
                    title = "Trending",
                    icon = painterResource(R.drawable.fire)
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            items(trendingPlaylists){ playlist ->

                MusicPlaylistCard(
                    title = playlist.title,
                    genre = playlist.genre,
                    firstSong = playlist.songs.firstOrNull()?.title ?: "Unknown",
                    songs = "${playlist.songs.size} songs",
                    duration = "1h 12min",
                    likes = playlist.likes.toString()
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
            item {
                Spacer(modifier.height(16.dp))
            }
            items(weatherPlaylists){ playlist ->

                MusicPlaylistCard(
                    title = playlist.title,
                    genre = playlist.genre,
                    firstSong = playlist.songs.firstOrNull()?.title ?: "Unknown",
                    songs = "${playlist.songs.size} songs",
                    duration = "1h 12min",
                    likes = playlist.likes.toString()
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            item {
                Spacer(modifier.height(16.dp))
            }
            item {
                SectionTitle(
                    title = "Music History",
                    icon = painterResource(R.drawable.music)
                )
            }
            item {
                Spacer(modifier.height(16.dp))
            }





            item {
                MusicHistoryCard(
                    lastPlayed = "Uptown Jazz",
                    onClick = {
                        rootNavController.navigate(Routes.MusicHistory)
                    }
                )
            }
            item {
                Spacer(modifier.height(16.dp))
            }
            item {
                SectionTitle(
                    title = "Genres",
                    icon = painterResource(R.drawable.headphones)
                )
            }
            item {

                CategoryFilterRow(
                    categories = listOf(
                        "Lo-fi",
                        "Jazz",
                        "Rock",
                        "Classical",
                        "Pop",
                        "Hip-Pop"
                    ),
                    selectedCategory = selectedGenre,
                    onCategorySelected = {
                        selectedGenre = it
                    }
                )

            }
            item{
                Spacer(modifier.height(24.dp))
            }
            item {
                SectionTitle(
                    title = "Recommended For You",
                    icon = painterResource(R.drawable.stars)
                )
            }
            item{
                Spacer(modifier.height(16.dp))
            }
            item {

                recommendedPlaylist?.let { playlist ->

                    RecommendedMusicCard(
                        id = playlist.id,

                        image = painterResource(playlist.albumImage),

                        title = playlist.title,

                        genre = playlist.genre,

                        mood = playlist.mood,

                        likes = playlist.likes.toString(),

                        onPlayClick = {

                        },

                        onViewPlaylistClick = { id ->

                            rootNavController.navigate(
                                Routes.playlistRoute(id)
                            )

                        }
                    )

                }

            }


        }
    }
}