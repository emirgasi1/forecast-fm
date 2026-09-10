package com.emirgasic.forecastfm.core.ui.components.music

import com.emirgasic.forecastfm.data.model.Music

fun formatPlaylistDuration(songs: List<Music>): String {
    var totalSeconds = 0

    songs.forEach { song ->
        totalSeconds += song.duration
    }

    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60

    return if (hours > 0) {
        "${hours}h ${minutes}min"
    } else {
        "${minutes}min"
    }
}


fun formatSongDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (remainingSeconds > 0) {
        "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    } else {
        "$minutes:00"
    }
}