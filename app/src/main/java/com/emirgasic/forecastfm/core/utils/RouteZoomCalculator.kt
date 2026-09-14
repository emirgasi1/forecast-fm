package com.emirgasic.forecastfm.core.utils

object RouteZoomCalculator {

    fun calculateZoom(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Double {
        val latDiff = maxLat - minLat
        val lngDiff = maxLng - minLng
        val maxDiff = maxOf(latDiff, lngDiff)

        return when {
            maxDiff > 0.2 -> 10.0
            maxDiff > 0.1 -> 11.5
            maxDiff > 0.05 -> 12.5
            maxDiff > 0.02 -> 13.5
            maxDiff > 0.01 -> 14.0
            maxDiff > 0.005 -> 14.5
            else -> 15.0
        }
    }
}