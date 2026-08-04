package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.LocationDetails

class LocationDetailsRepository {


    fun getLocations(): List<LocationDetails> {

        return listOf(

            LocationDetails(
                location = "Baščaršija",
                description = "Historic streets, coffee shops and traditional Sarajevo atmosphere.",

                weatherIcon = R.drawable.sun,
                temperature = "22°C",
                condition = "Sunny",
                humidity = "45%",
                wind = "12 km/h",

                playlistTitle = "Coffee House Vibes",
                songs = listOf(
                    "Morning Jazz",
                    "City Walk",
                    "Warm Coffee"
                ),

                outfitTitle = "Light Jacket + Jeans",
                outfitDescription = "Perfect for a sunny day with cooler evening temperatures."
            ),


            LocationDetails(
                location = "Trebević",
                description = "Mountain views with fresh air and a calmer atmosphere.",

                weatherIcon = R.drawable.sunny_cloudy,
                temperature = "17°C",
                condition = "Windy",
                humidity = "55%",
                wind = "20 km/h",

                playlistTitle = "Mountain Chill",
                songs = listOf(
                    "Nature Sounds",
                    "Lo-fi Escape",
                    "Evening Ride"
                ),

                outfitTitle = "Jacket + Boots",
                outfitDescription = "Warmer layers recommended because of wind."
            )

        )
    }



    fun getLocationDetails(location: String): LocationDetails? {

        return getLocations()
            .find {
                it.location == location
            }
    }

}