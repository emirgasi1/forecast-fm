package com.emirgasic.forecastfm.utils

import com.emirgasic.forecastfm.R

object TagIconMapper {

    fun getIconForTag(tag: String): Int {
        return when (tag.lowercase().trim()) {
            "morning coffee" -> R.drawable.coffee
            "studying" -> R.drawable.books
            "late night walk" -> R.drawable.moon
            "rainy day" -> R.drawable.heavy_rain
            "reading" -> R.drawable.books
            "cozy evening" -> R.drawable.moon
            "relaxing" -> R.drawable.moon
            "working" -> R.drawable.books
            "afternoon" -> R.drawable.sun
            "winter walk" -> R.drawable.snow
            "cozy night" -> R.drawable.moon
            "hot chocolate" -> R.drawable.coffee
            "evening drive" -> R.drawable.moon
            "night out" -> R.drawable.music
            "party" -> R.drawable.fire
            "traditional evening" -> R.drawable.music
            "romantic dinner" -> R.drawable.heart
            "culture" -> R.drawable.books
            else -> R.drawable.music
        }
    }
}