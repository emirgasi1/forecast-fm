package com.emirgasic.forecastfm.data.model

object LayerGroups {
    val groups = mapOf(
        "Food & Drink" to setOf("cafe", "internet_cafe", "restaurant", "fast_food", "bakery"),
        "Bars & Nightlife" to setOf("bar", "pub", "hookah_lounge", "nightclub", "nightlife", "casino"),
        "Shopping" to setOf("shop", "supermarket", "marketplace", "clothes", "electronics", "jewelry", "florist", "bakery"),
        "Accommodation" to setOf("hotel", "hostel", "motel", "guest_house", "apartment"),
        "Culture & Education" to setOf("museum", "gallery", "theatre", "cinema", "arts_centre", "library", "school", "university"),
        "Health" to setOf("hospital", "clinic", "pharmacy", "dentist", "doctors", "veterinary"),
        "Parks & Nature" to setOf("park", "garden", "picnic_site", "camp_site", "viewpoint", "attraction"),
        "Transit" to setOf("bus_stop", "bus_station", "taxi", "fuel", "charging_station", "parking")
    )

    fun matches(category: String, groupName: String): Boolean {
        val group = groups[groupName] ?: return false
        return group.contains(category.lowercase()) || category.contains(groupName, ignoreCase = true)
    }
}