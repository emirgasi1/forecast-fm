package com.emirgasic.forecastfm.core.utils

object RouteFormatter {

    fun formatDistance(meters: Double): String {
        return if (meters < 1000) {
            "${meters.toInt()} m"
        } else {
            String.format("%.1f km", meters / 1000)
        }
    }

    fun formatDuration(seconds: Double): String {
        val minutes = (seconds / 60).toInt()
        return if (minutes < 60) {
            "$minutes min"
        } else {
            val hours = minutes / 60
            val mins = minutes % 60
            "$hours h $mins min"
        }
    }
}