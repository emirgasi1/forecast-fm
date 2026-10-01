package com.emirgasic.forecastfm.feature.map

import com.emirgasic.forecastfm.data.model.BusStation
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.repository.BusStationRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FullMapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var locationRepository: LocationRepository
    private lateinit var placeRepository: PlaceRepository
    private lateinit var busStationRepository: BusStationRepository
    private lateinit var viewModel: FullMapViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        locationRepository = mockk(relaxed = true)
        placeRepository = mockk(relaxed = true)
        busStationRepository = mockk(relaxed = true)

        viewModel = FullMapViewModel(
            locationRepository = locationRepository,
            placeRepository = placeRepository,
            busStationRepository = busStationRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial locations empty`() {
        assertTrue(viewModel.locations.value.isEmpty())
    }

    @Test
    fun `initial enabledLayers contains Venues and Places`() {
        assertEquals(setOf("Venues", "Places"), viewModel.enabledLayers.value)
    }

    @Test
    fun `initial markerGeoJson is empty FeatureCollection`() {
        assertEquals(
            """{"type":"FeatureCollection","features":[]}""",
            viewModel.markerGeoJson.value
        )
    }

    // ---------- loaders ----------

    @Test
    fun `loadLocations populates locations and rebuilds markers`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "A")
        )

        viewModel.loadLocations()
        advanceUntilIdle()

        assertEquals(1, viewModel.locations.value.size)
        assertEquals(1, viewModel.markers.value.count { it.type == "venue" })
    }

    @Test
    fun `loadPlaces populates places and rebuilds markers`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(
            place("p1"),
            place("p2")
        )

        viewModel.loadPlaces()
        advanceUntilIdle()

        assertEquals(2, viewModel.places.value.size)
        assertEquals(2, viewModel.markers.value.count { it.type == "place" })
    }

    @Test
    fun `loadBusStations populates stations and rebuilds markers`() = runTest {
        coEvery { busStationRepository.getAllBusStations() } returns listOf(
            fakeBusStation("bs-1", "Station A")
        )

        viewModel.loadBusStations()
        advanceUntilIdle()

        assertEquals(1, viewModel.busStations.value.size)
        assertEquals(1, viewModel.markers.value.count { it.type == "bus_station" })
    }

    @Test
    fun `loadLocations with exception does not crash`() = runTest {
        coEvery { locationRepository.getLocations() } throws RuntimeException("api down")

        viewModel.loadLocations()
        advanceUntilIdle()

        assertTrue(viewModel.locations.value.isEmpty())
    }

    // ---------- selectLocation ----------

    @Test
    fun `selectLocation updates state and clears selectedPlace`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(place("p1"))
        viewModel.loadPlaces()
        advanceUntilIdle()
        viewModel.selectPlace(place("p1"))
        assertEquals("p1", viewModel.selectedPlace.value?.id)

        viewModel.selectLocation(fakeLocation("loc-1", "A"))

        assertEquals("loc-1", viewModel.selectedLocation.value?.id)
        assertNull(viewModel.selectedPlace.value)
    }

    // ---------- selectPlace ----------

    @Test
    fun `selectPlace updates state and sets location from venueId`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "A")
        )
        viewModel.loadLocations()
        advanceUntilIdle()

        val place = place("p1", venueId = "loc-1")
        viewModel.selectPlace(place)

        assertEquals("p1", viewModel.selectedPlace.value?.id)
        assertEquals("loc-1", viewModel.selectedLocation.value?.id)
    }

    // ---------- filters ----------

    @Test
    fun `updateSearchQuery updates state and filters markers`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(
            place("p1", name = "Cafe Central"),
            place("p2", name = "Pizza Place")
        )
        viewModel.loadPlaces()
        advanceUntilIdle()

        viewModel.updateSearchQuery("Pizza")

        assertEquals("Pizza", viewModel.searchQuery.value)
        assertEquals(1, viewModel.filteredMarkers.value.size)
        assertEquals("p2", viewModel.filteredMarkers.value.first().id)
    }

    @Test
    fun `setVenueCategoryFilter filters places by canonical category`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(
            place("cafe", category = "Cafe"),
            place("restaurant", category = "Restaurant")
        )
        viewModel.loadPlaces()
        advanceUntilIdle()

        viewModel.setVenueCategoryFilter("Cafes")

        assertEquals("Cafes", viewModel.venueCategoryFilter.value)
        val placeMarkers = viewModel.filteredMarkers.value.filter { it.type == "place" }
        assertEquals(1, placeMarkers.size)
        assertEquals("cafe", placeMarkers.first().id)
    }

    @Test
    fun `setVenueCategoryFilter to All clears filter`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(
            place("cafe", category = "Cafe"),
            place("restaurant", category = "Restaurant")
        )
        viewModel.loadPlaces()
        advanceUntilIdle()

        viewModel.setVenueCategoryFilter("All")

        assertNull(viewModel.venueCategoryFilter.value)
        assertEquals(2, viewModel.filteredMarkers.value.filter { it.type == "place" }.size)
    }

    // ---------- toggleLayer ----------

    @Test
    fun `toggleLayer removes layer when present`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(place("p1"))
        viewModel.loadPlaces()
        advanceUntilIdle()

        viewModel.toggleLayer("Places")

        assertFalse("Places" in viewModel.enabledLayers.value)
        assertEquals(0, viewModel.filteredMarkers.value.count { it.type == "place" })
    }

    @Test
    fun `toggleLayer adds layer back`() {
        viewModel.toggleLayer("Venues")
        assertFalse("Venues" in viewModel.enabledLayers.value)

        viewModel.toggleLayer("Venues")
        assertTrue("Venues" in viewModel.enabledLayers.value)
    }

    // ---------- toggleFilter ----------

    @Test
    fun `toggleFilter adds and removes filter`() {
        viewModel.toggleFilter("Cafe")
        assertTrue("Cafe" in viewModel.selectedFilters.value)

        viewModel.toggleFilter("Cafe")
        assertFalse("Cafe" in viewModel.selectedFilters.value)
    }

    // ---------- markerGeoJson ----------

    @Test
    fun `markerGeoJson is updated after loading`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(place("p1"))

        viewModel.loadPlaces()
        advanceUntilIdle()

        val geoJson = viewModel.markerGeoJson.value
        assertTrue("GeoJSON should contain p1 id, got: $geoJson", geoJson.contains("p1"))
        assertTrue("GeoJSON should be FeatureCollection, got: $geoJson",
            geoJson.contains("FeatureCollection"))
    }
    // ---------- helpers ----------

    private fun fakeLocation(id: String, name: String) = LocationResponse(
        id = id,
        name = name,
        description = "",
        latitude = 43.85,
        longitude = 18.41
    )

    private fun place(
        id: String,
        name: String = "Place $id",
        category: String = "Cafe",
        venueId: String? = null,
        rating: Double = 4.0
    ) = Place(
        id = id,
        name = name,
        category = category,
        venueId = venueId,
        address = "",
        latitude = 43.85,
        longitude = 18.41,
        description = "",
        imageUrl = null,
        rating = rating
    )

    private fun fakeBusStation(id: String, name: String) = BusStation(
        id = id,
        name = name,
        latitude = 43.85,
        longitude = 18.41,
        lines = emptyList()
    )
}