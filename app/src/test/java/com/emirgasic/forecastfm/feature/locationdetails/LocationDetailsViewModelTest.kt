package com.emirgasic.forecastfm.feature.locationdetails

import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaceRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherData
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.network.location.LocationResponse
import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocationDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var locationRepository: LocationRepository
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var outfitRepository: OutfitRepository
    private lateinit var placeRepository: PlaceRepository
    private lateinit var viewModel: LocationDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        locationRepository = mockk()
        weatherRepository = mockk()
        playlistRepository = mockk(relaxed = true)
        outfitRepository = mockk(relaxed = true)
        placeRepository = mockk(relaxed = true)

        viewModel = LocationDetailsViewModel(
            locationRepository = locationRepository,
            weatherRepository = weatherRepository,
            playlistRepository = playlistRepository,
            outfitRepository = outfitRepository,
            placeRepository = placeRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial locationDetails is null`() {
        assertNull(viewModel.locationDetails.value)
    }

    @Test
    fun `initial outfits empty`() {
        assertTrue(viewModel.outfits.value.isEmpty())
    }

    @Test
    fun `initial placesCount is 0`() {
        assertEquals(0, viewModel.placesCount.value)
    }

    // ---------- loadLocation success ----------

    @Test
    fun `loadLocation success populates locationDetails`() = runTest {
        stubHappyPath()

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        val details = viewModel.locationDetails.value
        assertEquals("loc-1", details?.location?.id)
        assertEquals("Baščaršija", details?.location?.name)
        assertEquals("Sunny", details?.weather?.condition)
    }

    @Test
    fun `loadLocation populates playlist when repository returns one`() = runTest {
        stubHappyPath(playlist = fakePlaylist("pl-1", "Chill Vibes"))

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        assertEquals("Chill Vibes", viewModel.locationDetails.value?.playlist?.title)
    }

    @Test
    fun `loadLocation with null playlist still populates details`() = runTest {
        stubHappyPath(playlist = null)

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        assertEquals("loc-1", viewModel.locationDetails.value?.location?.id)
        assertNull(viewModel.locationDetails.value?.playlist)
    }

    @Test
    fun `loadLocation populates outfits`() = runTest {
        stubHappyPath()
        coEvery { outfitRepository.getOutfitsByWeather("Sunny") } returns listOf(
            fakeOutfit("o1"),
            fakeOutfit("o2")
        )

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        assertEquals(2, viewModel.outfits.value.size)
    }

    @Test
    fun `loadLocation populates placesCount`() = runTest {
        stubHappyPath()
        coEvery { placeRepository.getPlacesCountByVenue("loc-1") } returns 42

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        assertEquals(42, viewModel.placesCount.value)
    }

    @Test
    fun `loadLocation passes correct weather condition to outfitRepository`() = runTest {
        stubHappyPath()
        coEvery { outfitRepository.getOutfitsByWeather(any()) } returns emptyList()

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        coVerify(exactly = 1) { outfitRepository.getOutfitsByWeather("Sunny") }
    }

    @Test
    fun `loadLocation passes location name and weather condition to playlistRepository`() = runTest {
        stubHappyPath()

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        coVerify(exactly = 1) {
            playlistRepository.getRecommendedPlaylist("Baščaršija", "Sunny")
        }
    }

    // ---------- edge cases ----------

    @Test
    fun `loadLocation with unknown id does nothing`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "Baščaršija")
        )

        viewModel.loadLocation("unknown")
        advanceUntilIdle()

        assertNull(viewModel.locationDetails.value)
        coVerify(exactly = 0) { weatherRepository.getWeather(any(), any(), any()) }
    }

    @Test
    fun `loadLocation with empty locations does nothing`() = runTest {
        coEvery { locationRepository.getLocations() } returns emptyList()

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        assertNull(viewModel.locationDetails.value)
    }

    @Test
    fun `loadLocation with locationRepository exception does nothing`() = runTest {
        coEvery { locationRepository.getLocations() } throws RuntimeException("api down")

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        assertNull(viewModel.locationDetails.value)
    }

    @Test
    fun `loadLocation with weatherRepository exception does nothing`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "Baščaršija")
        )
        coEvery { weatherRepository.getWeather(any(), any(), any()) } throws
                RuntimeException("weather down")

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        assertNull(viewModel.locationDetails.value)
    }

    @Test
    fun `loadLocation with placeRepository exception still populates details`() = runTest {
        stubHappyPath()
        coEvery { placeRepository.getPlacesCountByVenue(any()) } throws
                RuntimeException("count down")

        viewModel.loadLocation("loc-1")
        advanceUntilIdle()

        // locationDetails is populated (set before placesCount)
        assertEquals("loc-1", viewModel.locationDetails.value?.location?.id)
        // placesCount stays 0 because the exception short-circuits before the assignment
        assertEquals(0, viewModel.placesCount.value)
    }

    // ---------- helpers ----------

    private suspend fun stubHappyPath(playlist: Playlist? = fakePlaylist("pl-1", "Default")) {
        coEvery { locationRepository.getLocations() } returns listOf(
            fakeLocation("loc-1", "Baščaršija")
        )
        coEvery { weatherRepository.getWeather("Baščaršija", 43.85, 18.41) } returns
                fakeWeatherData("Sunny")
        coEvery { playlistRepository.getRecommendedPlaylist(any(), any()) } returns playlist
        coEvery { outfitRepository.getOutfitsByWeather(any()) } returns emptyList()
        coEvery { placeRepository.getPlacesCountByVenue(any()) } returns 0
    }

    private fun fakeLocation(id: String, name: String) = LocationResponse(
        id = id,
        name = name,
        description = "Description",
        latitude = 43.85,
        longitude = 18.41
    )

    private fun fakeWeatherData(condition: String) = WeatherData(
        weather = Weather(
            location = "Baščaršija",
            temperature = "22°C",
            condition = condition,
            feelsLike = "23°C",
            humidity = "50%",
            wind = "5 km/h",
            uvIndex = "3",
            airQuality = "Good",
            icon = 0
        ),
        hourly = emptyList(),
        daily = emptyList()
    )

    private fun fakePlaylist(id: String, title: String) = Playlist(
        id = id,
        title = title,
        genre = "Rock",
        mood = "Chill",
        albumImageUrl = null,
        weather = "Sunny",
        temperature = "22",
        location = "Baščaršija",
        songs = emptyList(),
        likes = 0,
        spotifyUrl = null,
        youtubeUrl = null,
        bestFor = emptyList()
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
}