package com.emirgasic.forecastfm.core.utils


fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return if (remainingSeconds > 0) {
        "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    } else {
        "$minutes:00"
    }
}