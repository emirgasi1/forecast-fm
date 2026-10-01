package com.emirgasic.forecastfm.feature.settings.location

import com.emirgasic.forecastfm.network.location.LocationApi
import com.emirgasic.forecastfm.network.location.LocationResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class DefaultLocationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var locationApi: LocationApi
    private lateinit var viewModel: DefaultLocationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        locationApi = mockk()
        viewModel = DefaultLocationViewModel(locationApi = locationApi)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial locations is empty`() {
        assertTrue(viewModel.locations.value.isEmpty())
    }

    @Test
    fun `initial selected is empty`() {
        assertEquals("", viewModel.selected.value)
    }

    @Test
    fun `initial isLoading is true`() {
        assertTrue(viewModel.isLoading.value)
    }

    // ---------- loadLocations ----------

    @Test
    fun `loadLocations populates names and selects first`() = runTest {
        coEvery { locationApi.getLocations() } returns listOf(
            fakeLocation("Baščaršija"),
            fakeLocation("Ilidža")
        )

        viewModel.loadLocations()
        advanceUntilIdle()

        assertEquals(listOf("Baščaršija", "Ilidža"), viewModel.locations.value)
        assertEquals("Baščaršija", viewModel.selected.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadLocations does not override existing selection`() = runTest {
        viewModel.select("Ilidža")

        coEvery { locationApi.getLocations() } returns listOf(
            fakeLocation("Baščaršija"),
            fakeLocation("Ilidža")
        )

        viewModel.loadLocations()
        advanceUntilIdle()

        assertEquals("Ilidža", viewModel.selected.value)
    }

    @Test
    fun `loadLocations with empty response leaves selected empty`() = runTest {
        coEvery { locationApi.getLocations() } returns emptyList()

        viewModel.loadLocations()
        advanceUntilIdle()

        assertTrue(viewModel.locations.value.isEmpty())
        assertEquals("", viewModel.selected.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadLocations with exception sets isLoading false`() = runTest {
        coEvery { locationApi.getLocations() } throws RuntimeException("api down")

        viewModel.loadLocations()
        advanceUntilIdle()

        assertTrue(viewModel.locations.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- select ----------

    @Test
    fun `select updates selected`() {
        viewModel.select("Ilidža")
        assertEquals("Ilidža", viewModel.selected.value)
    }

    @Test
    fun `select can be called before loadLocations`() = runTest {
        viewModel.select("Custom Name")
        assertEquals("Custom Name", viewModel.selected.value)
    }

    // ---------- helpers ----------

    private fun fakeLocation(name: String) = LocationResponse(
        id = "loc-$name",
        name = name,
        description = "",
        latitude = 43.85,
        longitude = 18.41
    )
}