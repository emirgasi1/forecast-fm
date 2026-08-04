package com.emirgasic.forecastfm.data.repository

import com.emirgasic.forecastfm.data.model.PlaceRecommendation


class PlaceRecommendationRepository {


    fun getRecommendations(): List<PlaceRecommendation> {

        return listOf(

            PlaceRecommendation(
                id = "1",
                name = "Ministry of Ćejf",
                category = "Coffee",
                location = "Baščaršija",
                description = "A cozy coffee place with a traditional Sarajevo atmosphere.",
                suitableFor = listOf(
                    "Partner",
                    "Friends"
                ),
                weatherCondition = "Sunny",
                rating = 4.8
            ),


            PlaceRecommendation(
                id = "2",
                name = "The Four Rooms of Mrs. Safija",
                category = "Restaurant",
                location = "Sarajevo Center",
                description = "A great choice for a special dinner experience.",
                suitableFor = listOf(
                    "Partner",
                    "Family"
                ),
                weatherCondition = "Any",
                rating = 4.7
            ),


            PlaceRecommendation(
                id = "3",
                name = "Mala Kuhinja",
                category = "Restaurant",
                location = "Sarajevo",
                description = "Good food and a relaxed atmosphere.",
                suitableFor = listOf(
                    "Friends",
                    "Family"
                ),
                weatherCondition = "Rainy",
                rating = 4.6
            )

        )
    }


}