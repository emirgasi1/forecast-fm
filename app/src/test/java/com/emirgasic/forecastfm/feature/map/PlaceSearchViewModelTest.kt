package com.emirgasic.forecastfm.feature.map

import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceSearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: PlaceRepository
    private lateinit var viewModel: PlaceSearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        viewModel = PlaceSearchViewModel(repository = repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial query is empty`() {
        assertEquals("", viewModel.query.value)
    }

    @Test
    fun `initial results empty`() {
        assertTrue(viewModel.results.value.isEmpty())
    }

    @Test
    fun `initial isLoading false`() {
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `initial error is null`() {
        assertNull(viewModel.error.value)
    }

    // ---------- updateQuery: blank input ----------

    @Test
    fun `updateQuery with blank clears results and error`() = runTest {
        // Seed some state first
        coEvery { repository.searchPlaces("x") } returns listOf(place("p1"))
        viewModel.updateQuery("x")
        advanceTimeBy(350)
        advanceUntilIdle()
        assertFalse(viewModel.results.value.isEmpty())

        viewModel.updateQuery("")

        assertEquals("", viewModel.query.value)
        assertTrue(viewModel.results.value.isEmpty())
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `updateQuery with blank does not call repository`() = runTest {
        viewModel.updateQuery("")
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.searchPlaces(any()) }
    }

    // ---------- updateQuery: non-blank ----------

    @Test
    fun `updateQuery waits for debounce before searching`() = runTest {
        coEvery { repository.searchPlaces("cafe") } returns listOf(place("p1"))

        viewModel.updateQuery("cafe")

        // Before debounce completes
        advanceTimeBy(100)
        coVerify(exactly = 0) { repository.searchPlaces("cafe") }

        // After debounce
        advanceTimeBy(300)
        advanceUntilIdle()
        coVerify(exactly = 1) { repository.searchPlaces("cafe") }
    }

    @Test
    fun `updateQuery success populates results`() = runTest {
        coEvery { repository.searchPlaces("cafe") } returns listOf(
            place("p1"),
            place("p2")
        )

        viewModel.updateQuery("cafe")
        advanceTimeBy(350)
        advanceUntilIdle()

        assertEquals("cafe", viewModel.query.value)
        assertEquals(2, viewModel.results.value.size)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `updateQuery rapid calls only search the last query`() = runTest {
        coEvery { repository.searchPlaces(any()) } returns emptyList()

        viewModel.updateQuery("c")
        viewModel.updateQuery("ca")
        viewModel.updateQuery("caf")
        viewModel.updateQuery("cafe")

        advanceTimeBy(350)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.searchPlaces("cafe") }
    }

    @Test
    fun `updateQuery with repository exception sets error and clears results`() = runTest {
        coEvery { repository.searchPlaces(any()) } throws RuntimeException("api down")

        viewModel.updateQuery("cafe")
        advanceTimeBy(350)
        advanceUntilIdle()

        assertEquals("api down", viewModel.error.value)
        assertTrue(viewModel.results.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `updateQuery with repository exception using null message uses fallback`() = runTest {
        coEvery { repository.searchPlaces(any()) } throws RuntimeException()

        viewModel.updateQuery("cafe")
        advanceTimeBy(350)
        advanceUntilIdle()

        assertEquals("Search failed", viewModel.error.value)
    }

    // ---------- clear ----------

    @Test
    fun `clear resets all state`() = runTest {
        coEvery { repository.searchPlaces(any()) } returns listOf(place("p1"))
        viewModel.updateQuery("cafe")
        advanceTimeBy(350)
        advanceUntilIdle()

        viewModel.clear()

        assertEquals("", viewModel.query.value)
        assertTrue(viewModel.results.value.isEmpty())
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `clear cancels an in-flight search`() = runTest {
        viewModel.updateQuery("cafe")
        viewModel.clear()

        advanceTimeBy(350)
        advanceUntilIdle()

        coVerify(exactly = 0) { repository.searchPlaces(any()) }
    }

    // ---------- helpers ----------

    private fun place(id: String) = Place(
        id = id,
        name = "Place $id",
        category = "Cafe",
        venueId = null,
        address = "",
        latitude = 43.85,
        longitude = 18.41,
        description = "",
        imageUrl = null,
        rating = 4.0
    )
}