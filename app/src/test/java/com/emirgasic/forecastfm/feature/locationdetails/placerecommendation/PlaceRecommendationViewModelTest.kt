package com.emirgasic.forecastfm.feature.locationdetails.placerecommendation

import com.emirgasic.forecastfm.data.model.PlaceRecommendation
import com.emirgasic.forecastfm.data.repository.PlaceRecommendationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceRecommendationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: PlaceRecommendationRepository
    private lateinit var viewModel: PlaceRecommendationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = PlaceRecommendationViewModel(repository = repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial recommendations empty`() {
        assertTrue(viewModel.recommendations.value.isEmpty())
    }

    @Test
    fun `initial filters are empty strings`() {
        assertEquals("", viewModel.selectedCategory.value)
        assertEquals("", viewModel.selectedCompanion.value)
        assertEquals("", viewModel.selectedWeather.value)
        assertEquals("", viewModel.selectedAgeGroup.value)
    }

    @Test
    fun `initial isLoading false`() {
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- loadForVenue ----------

    @Test
    fun `loadForVenue populates recommendations`() = runTest {
        coEvery { repository.getRecommendationsByVenue("venue-1") } returns listOf(
            place("p1"),
            place("p2")
        )

        viewModel.loadForVenue("venue-1")
        advanceUntilIdle()

        assertEquals(2, viewModel.recommendations.value.size)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadForVenue is idempotent for same venue`() = runTest {
        coEvery { repository.getRecommendationsByVenue(any()) } returns listOf(place("p1"))

        viewModel.loadForVenue("venue-1")
        advanceUntilIdle()
        viewModel.loadForVenue("venue-1")
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getRecommendationsByVenue("venue-1") }
    }

    @Test
    fun `loadForVenue reloads for different venue`() = runTest {
        coEvery { repository.getRecommendationsByVenue(any()) } returns listOf(place("p1"))

        viewModel.loadForVenue("venue-1")
        advanceUntilIdle()
        viewModel.loadForVenue("venue-2")
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getRecommendationsByVenue("venue-1") }
        coVerify(exactly = 1) { repository.getRecommendationsByVenue("venue-2") }
    }

    @Test
    fun `loadForVenue with exception leaves recommendations empty`() = runTest {
        coEvery { repository.getRecommendationsByVenue(any()) } throws
                RuntimeException("api down")

        viewModel.loadForVenue("venue-1")
        advanceUntilIdle()

        assertTrue(viewModel.recommendations.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- selectCategory ----------

    @Test
    fun `selectCategory sets filter and filters results`() = runTest {
        seedWith(
            place("p1", category = "Cafe"),
            place("p2", category = "Restaurant")
        )

        viewModel.selectCategory("Cafe")

        assertEquals("Cafe", viewModel.selectedCategory.value)
        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("p1", viewModel.recommendations.value.first().id)
    }

    @Test
    fun `selectCategory toggles off when same value`() = runTest {
        seedWith(place("p1", category = "Cafe"))

        viewModel.selectCategory("Cafe")
        assertEquals(1, viewModel.recommendations.value.size)

        viewModel.selectCategory("Cafe")

        assertEquals("", viewModel.selectedCategory.value)
        assertEquals(1, viewModel.recommendations.value.size)  // all shown again
    }

    @Test
    fun `selectCategory case insensitive`() = runTest {
        seedWith(place("p1", category = "Cafe"))

        viewModel.selectCategory("cafe")

        assertEquals(1, viewModel.recommendations.value.size)
    }

    // ---------- selectCompanion ----------

    @Test
    fun `selectCompanion filters by suitableFor list`() = runTest {
        seedWith(
            place("p1", suitableFor = listOf("Friends", "Partner")),
            place("p2", suitableFor = listOf("Alone"))
        )

        viewModel.selectCompanion("Friends")

        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("p1", viewModel.recommendations.value.first().id)
    }

    @Test
    fun `selectCompanion toggles off when same value`() = runTest {
        seedWith(place("p1", suitableFor = listOf("Friends")))

        viewModel.selectCompanion("Friends")
        viewModel.selectCompanion("Friends")

        assertEquals("", viewModel.selectedCompanion.value)
        assertEquals(1, viewModel.recommendations.value.size)
    }

    // ---------- selectWeather ----------

    @Test
    fun `selectWeather filters by weatherCondition`() = runTest {
        seedWith(
            place("p1", weatherCondition = "Sunny"),
            place("p2", weatherCondition = "Rainy")
        )

        viewModel.selectWeather("Sunny")

        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("p1", viewModel.recommendations.value.first().id)
    }

    @Test
    fun `selectWeather matches Any as universal`() = runTest {
        seedWith(
            place("p1", weatherCondition = "Any"),
            place("p2", weatherCondition = "Rainy")
        )

        viewModel.selectWeather("Sunny")

        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("p1", viewModel.recommendations.value.first().id)
    }

    // ---------- selectAgeGroup ----------

    @Test
    fun `selectAgeGroup filters by ageGroup list`() = runTest {
        seedWith(
            place("p1", ageGroup = listOf("Young Adults")),
            place("p2", ageGroup = listOf("Seniors"))
        )

        viewModel.selectAgeGroup("Young Adults")

        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("p1", viewModel.recommendations.value.first().id)
    }

    @Test
    fun `selectAgeGroup matches All Ages as universal`() = runTest {
        seedWith(
            place("p1", ageGroup = listOf("All Ages")),
            place("p2", ageGroup = listOf("Seniors"))
        )

        viewModel.selectAgeGroup("Teens")

        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("p1", viewModel.recommendations.value.first().id)
    }

    // ---------- combined filters ----------

    @Test
    fun `combined filters apply AND logic`() = runTest {
        seedWith(
            place("p1", category = "Cafe", weatherCondition = "Sunny"),
            place("p2", category = "Cafe", weatherCondition = "Rainy"),
            place("p3", category = "Restaurant", weatherCondition = "Sunny")
        )

        viewModel.selectCategory("Cafe")
        viewModel.selectWeather("Sunny")

        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("p1", viewModel.recommendations.value.first().id)
    }

    // ---------- resetFilters ----------

    @Test
    fun `resetFilters clears all filters and restores all`() = runTest {
        seedWith(
            place("p1", category = "Cafe"),
            place("p2", category = "Restaurant")
        )

        viewModel.selectCategory("Cafe")
        assertEquals(1, viewModel.recommendations.value.size)

        viewModel.resetFilters()

        assertEquals("", viewModel.selectedCategory.value)
        assertEquals("", viewModel.selectedCompanion.value)
        assertEquals("", viewModel.selectedWeather.value)
        assertEquals("", viewModel.selectedAgeGroup.value)
        assertEquals(2, viewModel.recommendations.value.size)
    }

    // ---------- helpers ----------

    private fun TestScope.seedWith(vararg places: PlaceRecommendation) {
        coEvery { repository.getRecommendationsByVenue("venue-1") } returns places.toList()
        viewModel.loadForVenue("venue-1")
        advanceUntilIdle()
    }

    private fun place(
        id: String,
        placeId: String = "place-$id",
        category: String = "Cafe",
        suitableFor: List<String> = listOf("Friends"),
        weatherCondition: String = "Sunny",
        ageGroup: List<String> = listOf("Adults")
    ) = PlaceRecommendation(
        id = id,
        placeId = placeId,
        name = "Place $id",
        category = category,
        location = "Baščaršija",
        description = "",
        imageUrl = null,
        rating = 4.5,
        suitableFor = suitableFor,
        weatherCondition = weatherCondition,
        ageGroup = ageGroup
    )
}