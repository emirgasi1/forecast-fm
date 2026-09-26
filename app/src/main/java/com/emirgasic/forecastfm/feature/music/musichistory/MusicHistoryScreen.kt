package com.emirgasic.forecastfm.feature.music.musichistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.ui.components.common.LoadingScreen
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle
import com.emirgasic.forecastfm.core.ui.components.music.musichistory.MusicHistoryEntryCard
import com.emirgasic.forecastfm.ui.theme.LocalForecastColors

@Composable
fun MusicHistoryScreen(
    navController: NavController,
    tokenManager: TokenManager,
    modifier: Modifier = Modifier,
    viewModel: MusicHistoryViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MusicHistoryViewModel(tokenManager) as T
            }
        }
    )
) {
    val history by viewModel.history.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val forecastColors = LocalForecastColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 60.dp, start = 10.dp, end = 10.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingScreen()
            }
        } else if (history.isEmpty()) {
            Text(
                text = "No music history yet.\nStart listening to playlists!",
                color = forecastColors.body,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            val groupedHistory = history.groupBy { it.section }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                groupedHistory.forEach { (section, entries) ->
                    item {
                        SectionTitle(title = section)
                    }

                    items(entries) { entry ->
                        MusicHistoryEntryCard(
                            playlist = entry.title,
                            weatherIcon = painterResource(entry.weatherIcon),
                            weather = entry.weather,
                            temperature = entry.temperature,
                            location = entry.location,
                            time = entry.time
                        )
                    }
                }
            }
        }
    }
}