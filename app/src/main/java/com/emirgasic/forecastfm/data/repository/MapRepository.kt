package com.emirgasic.forecastfm.data.repository



import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.MapRecommendation

class MapRepository {

    fun getRecommendations(): List<MapRecommendation> {

        return listOf(
            MapRecommendation(
                location = "Baščaršija",
                temperature = "22°C",
                weather = "Sunny",
                weatherIcon = R.drawable.sun,
                music = "Coffee House Vibes",
                outfit = "Light Jacket + Jeans"
            ),

            MapRecommendation(
                location = "Ilidža",
                temperature = "21°C",
                weather = "Cloudy",
                weatherIcon = R.drawable.sunny_cloudy,
                music = "Relaxing Afternoon",
                outfit = "Hoodie + Sneakers"
            )
        )
    }

    fun getRecommendation(location: String): MapRecommendation? {

        return getRecommendations()
            .find {
                it.location == location
            }
    }
}