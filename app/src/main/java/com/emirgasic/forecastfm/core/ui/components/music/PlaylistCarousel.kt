package com.emirgasic.forecastfm.core.ui.components.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.common.PagerIndicator
import com.emirgasic.forecastfm.data.model.Playlist

@Composable
fun PlaylistCarousel(
    playlists: List<Playlist>,
    favoritePlaylistIds: Set<String>,
    onFavoriteClick: (String) -> Unit,
    onPlayClick: (Playlist) -> Unit,
    onClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {

    if (playlists.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { playlists.size })

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 0.dp),
            pageSpacing = 12.dp
        ) { page ->
            val playlist = playlists[page]
            MusicPlaylistCard(
                title = playlist.title,
                genre = playlist.genre,
                mood = playlist.mood,
                weather = playlist.weather,
                temperature = playlist.temperature,
                location = playlist.location,
                likes = playlist.likes.toString(),
                isFavorite = playlist.id in favoritePlaylistIds,
                onFavoriteClick = { onFavoriteClick(playlist.id) },
                onClick = { onClick(playlist) },
                onPlayClick = { onPlayClick(playlist) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        PagerIndicator(
            pageCount = playlists.size,
            currentPage = pagerState.currentPage
        )
    }
}