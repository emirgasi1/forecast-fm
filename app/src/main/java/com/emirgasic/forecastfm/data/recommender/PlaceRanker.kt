package com.emirgasic.forecastfm.data.recommender

import com.emirgasic.forecastfm.data.model.Place

object PlaceRanker {

    private const val SCORE_CATEGORY_MATCH = 10

    fun canonicalCategory(category: String?): String? {
        return when (category?.trim()?.lowercase()) {
            null, "", "all" -> null

            "cafe", "cafes", "coffee", "coffee shop", "coffee shops" -> "cafe"
            "restaurant", "restaurants" -> "restaurant"
            "bar", "bars", "pub", "pubs" -> "bar"
            "nightlife", "night club", "nightclub", "nightclubs" -> "nightlife"
            "shop", "shops", "shopping", "store", "stores" -> "shop"
            "park", "parks", "parks & nature" -> "park"
            "museum", "museums" -> "museum"
            "hotel", "hotels" -> "hotel"
            "landmark", "landmarks" -> "landmark"
            "culture", "cultures", "historic & culture" -> "culture"
            "activity", "activities" -> "activity"
            "outdoor", "outdoors" -> "outdoor"
            "viewpoint", "viewpoints" -> "viewpoint"
            "attraction", "attractions" -> "attraction"
            "bus stop", "bus stops" -> "bus stop"

            else -> category.trim().lowercase()
        }
    }

    fun categoriesMatch(a: String?, b: String?): Boolean {
        return canonicalCategory(a) == canonicalCategory(b)
    }

    fun scorePlace(place: Place, preferredCategories: Set<String>): Int {
        return if (preferredCategories.any { categoriesMatch(place.category, it) }) {
            SCORE_CATEGORY_MATCH
        } else {
            0
        }
    }

    fun rankByPreferences(
        places: List<Place>,
        preferredCategories: Set<String>
    ): List<Place> {
        if (places.isEmpty()) return emptyList()

        return if (preferredCategories.isEmpty()) {
            places.sortedBy { it.name }
        } else {
            places.sortedWith(
                compareByDescending<Place> { scorePlace(it, preferredCategories) }
                    .thenBy { it.name }
            )
        }
    }
}