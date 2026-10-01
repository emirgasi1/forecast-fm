package com.emirgasic.forecastfm.feature.place

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.SavedPlaceRepository
import com.emirgasic.forecastfm.network.location.LocationResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
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
class PlaceInfoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var placeRepository: PlaceRepository
    private lateinit var savedPlaceRepository: SavedPlaceRepository
    private lateinit var locationRepository: LocationRepository
    private lateinit var viewModel: PlaceInfoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        placeRepository = mockk()
        savedPlaceRepository = mockk(relaxed = true)
        locationRepository = mockk(relaxed = true)

        every { tokenManager.getUserId() } returns flowOf("user-123")

        viewModel = PlaceInfoViewModel(
            tokenManager = tokenManager,
            placeRepository = placeRepository,
            savedPlaceRepository = savedPlaceRepository,
            locationRepository = locationRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial place is null`() {
        assertNull(viewModel.place.value)
    }

    @Test
    fun `initial isLoading is true`() {
        assertTrue(viewModel.isLoading.value)
    }

    @Test
    fun `initial isSaved is false`() {
        assertFalse(viewModel.isSaved.value)
    }

    @Test
    fun `initial locationName is null`() {
        assertNull(viewModel.locationName.value)
    }

    // ---------- loadPlace ----------

    @Test
    fun `loadPlace success populates place`() = runTest {
        coEvery { placeRepository.getPlaceById("p1") } returns fakePlace("p1")
        coEvery { savedPlaceRepository.isPlaceSaved(any(), any()) } returns false

        viewModel.loadPlace("p1")
        advanceUntilIdle()

        assertEquals("p1", viewModel.place.value?.id)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadPlace with venueId fetches location name`() = runTest {
        coEvery { placeRepository.getPlaceById("p1") } returns
                fakePlace("p1", venueId = "loc-1")
        coEvery { locationRepository.getLocationById("loc-1") } returns
                fakeLocation("loc-1", "Baščaršija")
        coEvery { savedPlaceRepository.isPlaceSaved(any(), any()) } returns false

        viewModel.loadPlace("p1")
        advanceUntilIdle()

        assertEquals("Baščaršija", viewModel.locationName.value)
    }

    @Test
    fun `loadPlace with null venueId leaves locationName null`() = runTest {
        coEvery { placeRepository.getPlaceById("p1") } returns
                fakePlace("p1", venueId = null)
        coEvery { savedPlaceRepository.isPlaceSaved(any(), any()) } returns false

        viewModel.loadPlace("p1")
        advanceUntilIdle()

        assertNull(viewModel.locationName.value)
        coVerify(exactly = 0) { locationRepository.getLocationById(any()) }
    }

    @Test
    fun `loadPlace populates isSaved from repository`() = runTest {
        coEvery { placeRepository.getPlaceById("p1") } returns fakePlace("p1")
        coEvery { savedPlaceRepository.isPlaceSaved("p1", "user-123") } returns true

        viewModel.loadPlace("p1")
        advanceUntilIdle()

        assertTrue(viewModel.isSaved.value)
    }

    @Test
    fun `loadPlace with no user does not check saved status`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)
        coEvery { placeRepository.getPlaceById("p1") } returns fakePlace("p1")

        viewModel.loadPlace("p1")
        advanceUntilIdle()

        coVerify(exactly = 0) { savedPlaceRepository.isPlaceSaved(any(), any()) }
        assertFalse(viewModel.isSaved.value)
    }

    @Test
    fun `loadPlace when repository returns null leaves place null`() = runTest {
        coEvery { placeRepository.getPlaceById("missing") } returns null

        viewModel.loadPlace("missing")
        advanceUntilIdle()

        assertNull(viewModel.place.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadPlace with exception sets isLoading false`() = runTest {
        coEvery { placeRepository.getPlaceById(any()) } throws RuntimeException("api down")

        viewModel.loadPlace("p1")
        advanceUntilIdle()

        assertNull(viewModel.place.value)
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- toggleSave ----------

    @Test
    fun `toggleSave when not saved calls savePlace and sets isSaved true`() = runTest {
        coEvery { placeRepository.getPlaceById("p1") } returns fakePlace("p1")
        coEvery { savedPlaceRepository.isPlaceSaved(any(), any()) } returns false
        viewModel.loadPlace("p1")
        advanceUntilIdle()
        assertFalse(viewModel.isSaved.value)

        viewModel.toggleSave()
        advanceUntilIdle()

        coVerify(exactly = 1) { savedPlaceRepository.savePlace("p1", "user-123") }
        assertTrue(viewModel.isSaved.value)
    }

    @Test
    fun `toggleSave when already saved calls unsavePlace and sets isSaved false`() = runTest {
        coEvery { placeRepository.getPlaceById("p1") } returns fakePlace("p1")
        coEvery { savedPlaceRepository.isPlaceSaved(any(), any()) } returns true
        viewModel.loadPlace("p1")
        advanceUntilIdle()
        assertTrue(viewModel.isSaved.value)

        viewModel.toggleSave()
        advanceUntilIdle()

        coVerify(exactly = 1) { savedPlaceRepository.unsavePlace("p1", "user-123") }
        assertFalse(viewModel.isSaved.value)
    }

    @Test
    fun `toggleSave before loadPlace does nothing`() = runTest {
        viewModel.toggleSave()
        advanceUntilIdle()

        coVerify(exactly = 0) { savedPlaceRepository.savePlace(any(), any()) }
        coVerify(exactly = 0) { savedPlaceRepository.unsavePlace(any(), any()) }
    }

    @Test
    fun `toggleSave with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)
        coEvery { placeRepository.getPlaceById("p1") } returns fakePlace("p1")
        viewModel.loadPlace("p1")
        advanceUntilIdle()

        viewModel.toggleSave()
        advanceUntilIdle()

        coVerify(exactly = 0) { savedPlaceRepository.savePlace(any(), any()) }
        coVerify(exactly = 0) { savedPlaceRepository.unsavePlace(any(), any()) }
    }

    @Test
    fun `toggleSave with repository exception leaves state unchanged`() = runTest {
        coEvery { placeRepository.getPlaceById("p1") } returns fakePlace("p1")
        coEvery { savedPlaceRepository.isPlaceSaved(any(), any()) } returns false
        viewModel.loadPlace("p1")
        advanceUntilIdle()

        coEvery { savedPlaceRepository.savePlace(any(), any()) } throws
                RuntimeException("api down")

        viewModel.toggleSave()
        advanceUntilIdle()

        assertFalse(viewModel.isSaved.value)
    }

    // ---------- helpers ----------

    private fun fakePlace(id: String, venueId: String? = null) = Place(
        id = id,
        name = "Place $id",
        category = "Cafe",
        venueId = venueId,
        address = "",
        latitude = 43.85,
        longitude = 18.41,
        description = "",
        imageUrl = null,
        rating = 4.0
    )

    private fun fakeLocation(id: String, name: String) = LocationResponse(
        id = id,
        name = name,
        description = "",
        latitude = 43.85,
        longitude = 18.41
    )
}