package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.model.Style

class StyleRepository {

    fun getStyle(): Style {

        return Style(

            outfits = listOf(

                Outfit(
                    id = "1",
                    image = R.drawable.profile_picture, // Replace later
                    title = "Coffee Morning",
                    weatherCondition = "Sunny",
                    season = "Spring"
                ),

                Outfit(
                    id = "2",
                    image = R.drawable.profile_picture,
                    title = "Sarajevo Casual",
                    weatherCondition = "Cloudy",
                    season = "Autumn"
                ),

                Outfit(
                    id = "3",
                    image = R.drawable.profile_picture,
                    title = "Rainy Walk",
                    weatherCondition = "Rainy",
                    season = "Autumn"
                ),

                Outfit(
                    id = "4",
                    image = R.drawable.profile_picture,
                    title = "Night Out",
                    weatherCondition = "Clear",
                    season = "Summer"
                )

            )

        )

    }

}