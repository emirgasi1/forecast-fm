package com.emirgasic.forecastfm.core.ui.components.music.musichistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.emirgasic.forecastfm.core.ui.components.common.SectionTitle

@Composable
fun HistorySection(
    title: String,
    playlist: String,
    weatherIcon: Painter,
    weather: String,
    temperature: String,
    location: String,
    time: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        SectionTitle(
            title = title
        )

        MusicHistoryEntryCard(
            playlist = playlist,
            weatherIcon = weatherIcon,
            weather = weather,
            temperature = temperature,
            location = location,
            time = time
        )

    }

}