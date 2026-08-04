package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.R
import com.emirgasic.forecastfm.data.model.Outfit

class OutfitRepository {

    fun getOutfits(): List<Outfit> {
        return listOf(
            Outfit(
                id = "1",
                image = R.drawable.outfit1,
                title = "Coffee Walk",
                weatherCondition = "Sunny",
                season = "Spring"
            ),
            Outfit(
                id = "2",
                image = R.drawable.outfit2,
                title = "Rainy Day",
                weatherCondition = "Rain",
                season = "Autumn"
            ),
            Outfit(
                id = "3",
                image = R.drawable.outfit3,
                title = "Winter Cozy",
                weatherCondition = "Snow",
                season = "Winter"
            )
        )
    }
}