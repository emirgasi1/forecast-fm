package com.emirgasic.forecastfm.feature.map

import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.data.model.BusStation
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.model.Place
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.BusStationRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
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
class MapViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var onboardingPrefs: OnboardingPreferences
    private lateinit var locationRepository: LocationRepository
    private lateinit var outfitRepository: OutfitRepository
    private lateinit var placeRepository: PlaceRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var busStationRepository: BusStationRepository
    private lateinit var viewModel: MapViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        onboardingPrefs = mockk(relaxed = true)
        locationRepository = mockk(relaxed = true)
        outfitRepository = mockk(relaxed = true)
        placeRepository = mockk(relaxed = true)
        playlistRepository = mockk(relaxed = true)
        busStationRepository = mockk(relaxed = true)

        every { onboardingPrefs.placeCategories } returns flowOf(emptySet())

        viewModel = MapViewModel(
            onboardingPrefs = onboardingPrefs,
            locationRepository = locationRepository,
            outfitRepository = outfitRepository,
            placeRepository = placeRepository,
            playlistRepository = playlistRepository,
            busStationRepository = busStationRepository
        )
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
    fun `initial selectedLocation is null`() {
        assertNull(viewModel.selectedLocation.value)
    }

    @Test
    fun `initial markers is empty`() {
        assertTrue(viewModel.markers.value.isEmpty())
    }

    @Test
    fun `initial enabledLayers contains default layers`() {
        assertEquals(setOf("Venues", "Places", "Bus Stops"), viewModel.enabledLayers.value)
    }

    @Test
    fun `initial venueCategoryFilter is null`() {
        assertNull(viewModel.venueCategoryFilter.value)
    }

    // ---------- loadLocations ----------

    @Test
    fun `loadLocations populates locations and selects first`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "Baščaršija"),
            fakeLocation("loc-2", "Ilidža")
        )

        viewModel.loadLocations()
        advanceUntilIdle()

        assertEquals(2, viewModel.locations.value.size)
        assertEquals("loc-1", viewModel.selectedLocation.value?.id)
    }

    @Test
    fun `loadLocations with exception leaves state unchanged`() = runTest {
        coEvery { locationRepository.getLocations() } throws RuntimeException("api down")

        viewModel.loadLocations()
        advanceUntilIdle()

        assertTrue(viewModel.locations.value.isEmpty())
    }

    // ---------- loadAllPlaces ----------

    @Test
    fun `loadAllPlaces populates places sorted by rating`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(
            place("p1", rating = 3.0),
            place("p2", rating = 5.0)
        )

        viewModel.loadAllPlaces()
        advanceUntilIdle()

        val places = viewModel.markers.value.filter { it.type == "place" }
        assertEquals(2, places.size)
        assertEquals("p2", places[0].id)
    }

    @Test
    fun `loadAllPlaces selects first place`() = runTest {
        coEvery { placeRepository.getAllPlaces() } returns listOf(
            place("p1", rating = 5.0)
        )

        viewModel.loadAllPlaces()
        advanceUntilIdle()

        assertEquals("p1", viewModel.selectedPlace.value?.id)
    }

    // ---------- loadBusStations ----------

    @Test
    fun `loadBusStations adds bus station markers`() = runTest {
        coEvery { busStationRepository.getAllBusStations() } returns listOf(
            fakeBusStation("bs-1", "Station A")
        )

        viewModel.loadBusStations()
        advanceUntilIdle()

        val busMarkers = viewModel.markers.value.filter { it.type == "bus_station" }
        assertEquals(1, busMarkers.size)
        assertEquals("bs-1", busMarkers[0].id)
    }

    // ---------- selectLocation ----------

    @Test
    fun `selectLocation updates selectedLocation`() {
        val location = fakeLocation("loc-1", "Baščaršija")
        viewModel.selectLocation(location)
        assertEquals("loc-1", viewModel.selectedLocation.value?.id)
    }

    @Test
    fun `selectLocation updates marker isSelected flags`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "A"),
            fakeLocation("loc-2", "B")
        )
        viewModel.loadLocations()
        advanceUntilIdle()

        viewModel.selectLocation(fakeLocation("loc-2", "B"))

        val markers = viewModel.markers.value.filter { it.type == "venue" }
        assertFalse(markers.first { it.id == "loc-1" }.isSelected)
        assertTrue(markers.first { it.id == "loc-2" }.isSelected)
    }

    // ---------- selectPlace ----------

    @Test
    fun `selectPlace updates selectedPlace`() {
        val place = place("p1")
        viewModel.selectPlace(place)
        assertEquals("p1", viewModel.selectedPlace.value?.id)
    }

    // ---------- search and filters ----------

    @Test
    fun `updateSearchQuery updates state`() {
        viewModel.updateSearchQuery("cafe")
        assertEquals("cafe", viewModel.searchQuery.value)
    }

    @Test
    fun `toggleFilter adds filter when not present`() {
        viewModel.toggleFilter("Cafe")
        assertTrue("Cafe" in viewModel.selectedFilters.value)
    }

    @Test
    fun `toggleFilter removes filter when present`() {
        viewModel.toggleFilter("Cafe")
        viewModel.toggleFilter("Cafe")
        assertFalse("Cafe" in viewModel.selectedFilters.value)
    }

    @Test
    fun `toggleLayer removes layer when present`() {
        viewModel.toggleLayer("Venues")
        assertFalse("Venues" in viewModel.enabledLayers.value)
    }

    @Test
    fun `toggleLayer adds layer when not present`() {
        viewModel.toggleLayer("Venues")
        viewModel.toggleLayer("Venues")
        assertTrue("Venues" in viewModel.enabledLayers.value)
    }

    @Test
    fun `setVenueCategoryFilter updates state`() {
        viewModel.setVenueCategoryFilter("Cafe")
        assertEquals("Cafe", viewModel.venueCategoryFilter.value)
    }

    @Test
    fun `setVenueCategoryFilter to null clears filter`() {
        viewModel.setVenueCategoryFilter("Cafe")
        viewModel.setVenueCategoryFilter(null)
        assertNull(viewModel.venueCategoryFilter.value)
    }

    // ---------- loadOutfits ----------

    @Test
    fun `loadOutfits populates outfits`() = runTest {
        coEvery { outfitRepository.getOutfitsByWeather("Sunny") } returns listOf(
            fakeOutfit("o1")
        )

        viewModel.loadOutfits("Sunny")
        advanceUntilIdle()

        assertEquals(1, viewModel.outfits.value.size)
    }

    @Test
    fun `loadOutfits with exception leaves outfits empty`() = runTest {
        coEvery { outfitRepository.getOutfitsByWeather(any()) } throws
                RuntimeException("api down")

        viewModel.loadOutfits("Sunny")
        advanceUntilIdle()

        assertTrue(viewModel.outfits.value.isEmpty())
    }

    // ---------- loadPlaylistForLocation ----------

    @Test
    fun `loadPlaylistForLocation populates playlistMap`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "Baščaršija")
        )
        viewModel.loadLocations()
        advanceUntilIdle()

        coEvery {
            playlistRepository.getRecommendedPlaylist("Baščaršija", "Sunny")
        } returns fakePlaylist("pl-1", "Chill Vibes")

        viewModel.loadPlaylistForLocation("loc-1", "Sunny")
        advanceUntilIdle()

        assertEquals("Chill Vibes", viewModel.playlistMap.value["loc-1"])
    }

    @Test
    fun `loadPlaylistForLocation with no matching playlist does not update map`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "Baščaršija")
        )
        viewModel.loadLocations()
        advanceUntilIdle()

        coEvery {
            playlistRepository.getRecommendedPlaylist(any(), any())
        } returns null

        viewModel.loadPlaylistForLocation("loc-1", "Sunny")
        advanceUntilIdle()

        assertTrue(viewModel.playlistMap.value.isEmpty())
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

    private fun fakeBusStation(id: String, name: String) = BusStation(
        id = id,
        name = name,
        latitude = 43.85,
        longitude = 18.41,
        lines = emptyList()
    )

    private fun fakeOutfit(id: String) = Outfit(
        id = id,
        userId = "user-123",
        imageUrl = "",
        title = "Outfit $id",
        weatherCondition = "Sunny",
        season = "Summer",
        likes = 0,
        storeName = null,
        storeAddress = null,
        price = null,
        storePhone = null,
        productUrl = null,
        createdAt = ""
    )

    private fun fakePlaylist(id: String, title: String) = Playlist(
        id = id,
        title = title,
        genre = "Rock",
        mood = "Chill",
        albumImageUrl = null,
        weather = "Sunny",
        temperature = "20",
        location = "Baščaršija",
        songs = emptyList(),
        likes = 0,
        spotifyUrl = null,
        youtubeUrl = null,
        bestFor = emptyList()
    )
}