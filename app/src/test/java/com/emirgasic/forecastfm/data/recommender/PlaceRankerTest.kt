package com.emirgasic.forecastfm.data.recommender

import com.emirgasic.forecastfm.data.model.Place
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceRankerTest {

    // ---------- canonicalCategory: null / blank / all ----------

    @Test
    fun `canonicalCategory returns null for null input`() {
        assertNull(PlaceRanker.canonicalCategory(null))
    }

    @Test
    fun `canonicalCategory returns null for blank input`() {
        assertNull(PlaceRanker.canonicalCategory(""))
        assertNull(PlaceRanker.canonicalCategory("   "))
    }

    @Test
    fun `canonicalCategory returns null for All`() {
        assertNull(PlaceRanker.canonicalCategory("All"))
        assertNull(PlaceRanker.canonicalCategory("all"))
    }

    // ---------- canonicalCategory: cafe variants ----------

    @Test
    fun `canonicalCategory handles cafe variants`() {
        assertEquals("cafe", PlaceRanker.canonicalCategory("Cafe"))
        assertEquals("cafe", PlaceRanker.canonicalCategory("cafes"))
        assertEquals("cafe", PlaceRanker.canonicalCategory("Coffee"))
        assertEquals("cafe", PlaceRanker.canonicalCategory("Coffee Shop"))
        assertEquals("cafe", PlaceRanker.canonicalCategory("  CAFE  "))
    }

    // ---------- canonicalCategory: bar variants ----------

    @Test
    fun `canonicalCategory handles bar variants`() {
        assertEquals("bar", PlaceRanker.canonicalCategory("Bar"))
        assertEquals("bar", PlaceRanker.canonicalCategory("bars"))
        assertEquals("bar", PlaceRanker.canonicalCategory("Pub"))
        assertEquals("bar", PlaceRanker.canonicalCategory("pubs"))
    }

    // ---------- canonicalCategory: museum / hotel / landmark / activity ----------

    @Test
    fun `canonicalCategory handles museum variants`() {
        assertEquals("museum", PlaceRanker.canonicalCategory("Museum"))
        assertEquals("museum", PlaceRanker.canonicalCategory("museums"))
    }

    @Test
    fun `canonicalCategory handles hotel variants`() {
        assertEquals("hotel", PlaceRanker.canonicalCategory("Hotel"))
        assertEquals("hotel", PlaceRanker.canonicalCategory("hotels"))
    }

    @Test
    fun `canonicalCategory handles landmark variants`() {
        assertEquals("landmark", PlaceRanker.canonicalCategory("Landmark"))
        assertEquals("landmark", PlaceRanker.canonicalCategory("landmarks"))
    }

    @Test
    fun `canonicalCategory handles activity variants`() {
        assertEquals("activity", PlaceRanker.canonicalCategory("Activity"))
        assertEquals("activity", PlaceRanker.canonicalCategory("activities"))
    }

    // ---------- canonicalCategory: restaurant / shop / nightlife ----------

    @Test
    fun `canonicalCategory handles restaurant variants`() {
        assertEquals("restaurant", PlaceRanker.canonicalCategory("Restaurant"))
        assertEquals("restaurant", PlaceRanker.canonicalCategory("restaurants"))
    }

    @Test
    fun `canonicalCategory handles shop variants`() {
        assertEquals("shop", PlaceRanker.canonicalCategory("Shop"))
        assertEquals("shop", PlaceRanker.canonicalCategory("shopping"))
        assertEquals("shop", PlaceRanker.canonicalCategory("store"))
        assertEquals("shop", PlaceRanker.canonicalCategory("stores"))
    }

    @Test
    fun `canonicalCategory handles nightlife variants`() {
        assertEquals("nightlife", PlaceRanker.canonicalCategory("Nightlife"))
        assertEquals("nightlife", PlaceRanker.canonicalCategory("nightclub"))
        assertEquals("nightlife", PlaceRanker.canonicalCategory("nightclubs"))
        assertEquals("nightlife", PlaceRanker.canonicalCategory("night club"))
    }

    // ---------- canonicalCategory: park variants (merged from both) ----------

    @Test
    fun `canonicalCategory handles park variants`() {
        assertEquals("park", PlaceRanker.canonicalCategory("Park"))
        assertEquals("park", PlaceRanker.canonicalCategory("parks"))
        assertEquals("park", PlaceRanker.canonicalCategory("Parks & Nature"))
    }

    // ---------- canonicalCategory: culture variants (merged) ----------

    @Test
    fun `canonicalCategory handles culture variants`() {
        assertEquals("culture", PlaceRanker.canonicalCategory("Culture"))
        assertEquals("culture", PlaceRanker.canonicalCategory("cultures"))
        assertEquals("culture", PlaceRanker.canonicalCategory("Historic & Culture"))
    }

    // ---------- canonicalCategory: bus stop ----------

    @Test
    fun `canonicalCategory handles bus stop variants`() {
        assertEquals("bus stop", PlaceRanker.canonicalCategory("Bus Stop"))
        assertEquals("bus stop", PlaceRanker.canonicalCategory("Bus Stops"))
    }

    // ---------- canonicalCategory: fallthrough ----------

    @Test
    fun `canonicalCategory falls through for unknown categories`() {
        assertEquals("karaoke bar", PlaceRanker.canonicalCategory("Karaoke Bar"))
        assertEquals("some custom", PlaceRanker.canonicalCategory("Some Custom"))
    }

    // ---------- categoriesMatch ----------

    @Test
    fun `categoriesMatch is true for same canonical form`() {
        assertTrue(PlaceRanker.categoriesMatch("Cafe", "cafes"))
        assertTrue(PlaceRanker.categoriesMatch("Park", "Parks & Nature"))
        assertTrue(PlaceRanker.categoriesMatch(null, "all"))
    }

    @Test
    fun `categoriesMatch is false for different categories`() {
        assertFalse(PlaceRanker.categoriesMatch("Cafe", "Restaurant"))
        assertFalse(PlaceRanker.categoriesMatch("Park", "Nightlife"))
    }

    // ---------- scorePlace ----------

    @Test
    fun `scorePlace returns 0 when no preferences set`() {
        assertEquals(0, PlaceRanker.scorePlace(place(category = "Cafe"), emptySet()))
    }

    @Test
    fun `scorePlace returns 10 when category matches a preference`() {
        assertEquals(10, PlaceRanker.scorePlace(place(category = "Cafe"), setOf("Cafe")))
    }

    @Test
    fun `scorePlace handles canonical matching`() {
        assertEquals(10, PlaceRanker.scorePlace(place(category = "Cafes"), setOf("cafe")))
    }

    @Test
    fun `scorePlace returns 0 when no preference matches`() {
        assertEquals(0, PlaceRanker.scorePlace(place(category = "Restaurant"), setOf("Cafe")))
    }

    @Test
    fun `scorePlace returns 10 when any preference matches`() {
        assertEquals(10, PlaceRanker.scorePlace(place(category = "Cafe"), setOf("Park", "Cafe", "Shop")))
    }

    // ---------- rankByPreferences ----------

    @Test
    fun `rankByPreferences returns empty for empty input`() {
        assertTrue(PlaceRanker.rankByPreferences(emptyList(), setOf("Cafe")).isEmpty())
    }

    @Test
    fun `rankByPreferences sorts by rating when no preferences`() {
        val places = listOf(
            place(id = "a", rating = 3.0),
            place(id = "b", rating = 5.0),
            place(id = "c", rating = 4.0)
        )
        val ranked = PlaceRanker.rankByPreferences(places, emptySet())
        assertEquals(listOf("b", "c", "a"), ranked.map { it.id })
    }

    @Test
    fun `rankByPreferences puts matching categories first`() {
        val places = listOf(
            place(id = "restaurant", category = "Restaurant", rating = 5.0),
            place(id = "cafe", category = "Cafe", rating = 3.0)
        )
        val ranked = PlaceRanker.rankByPreferences(places, setOf("Cafe"))
        assertEquals(listOf("cafe", "restaurant"), ranked.map { it.id })
    }

    @Test
    fun `rankByPreferences sorts by rating within matching groups`() {
        val places = listOf(
            place(id = "cafe-low", category = "Cafe", rating = 3.0),
            place(id = "cafe-high", category = "Cafe", rating = 5.0),
            place(id = "restaurant", category = "Restaurant", rating = 4.5)
        )
        val ranked = PlaceRanker.rankByPreferences(places, setOf("Cafe"))
        assertEquals(listOf("cafe-high", "cafe-low", "restaurant"), ranked.map { it.id })
    }

    // ---------- helpers ----------

    private fun place(
        id: String = "id",
        category: String = "Cafe",
        rating: Double = 4.0
    ) = Place(
        id = id,
        name = "Place $id",
        category = category,
        venueId = null,
        address = "",
        latitude = 43.85,
        longitude = 18.41,
        description = "",
        imageUrl = null,
        rating = rating
    )
}