package com.emirgasic.forecastfm.feature.home

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.repository.HomeRepository
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.WeatherData
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.network.location.LocationResponse
import io.mockk.coEvery
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var onboardingPrefs: OnboardingPreferences
    private lateinit var homeRepository: HomeRepository
    private lateinit var locationRepository: LocationRepository
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var playlistRepository: PlaylistRepository

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        tokenManager = mockk(relaxed = true)
        onboardingPrefs = mockk(relaxed = true)
        homeRepository = mockk(relaxed = true)
        locationRepository = mockk()
        weatherRepository = mockk()
        playlistRepository = mockk()

        every { tokenManager.getUserId() } returns flowOf("user-123")
        every { onboardingPrefs.musicGenres } returns flowOf(emptySet())
        every { onboardingPrefs.moods } returns flowOf(emptySet())
        every { onboardingPrefs.weatherPrefs } returns flowOf(emptySet())
        every { homeRepository.getGreeting() } returns "Good Morning"

        viewModel = HomeViewModel(
            tokenManager = tokenManager,
            onboardingPrefs = onboardingPrefs,
            homeRepository = homeRepository,
            locationRepository = locationRepository,
            weatherRepository = weatherRepository,
            playlistRepository = playlistRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `initial state is Loading`() {
        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
    }


    @Test
    fun `loadHome success populates Success with home data`() = runTest {
        stubHappyPath()

        viewModel.loadHome(null, null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Success but got $state", state is HomeUiState.Success)
        val home = (state as HomeUiState.Success).home
        assertEquals("Good Morning", home.greeting)
        assertEquals("Sunny", home.weather.condition)
        assertEquals(3, home.playlists.size)
    }

    @Test
    fun `loadHome uses first location when lat lng are null`() = runTest {
        stubHappyPath()

        viewModel.loadHome(null, null)
        advanceUntilIdle()

        val home = (viewModel.uiState.value as HomeUiState.Success).home

        assertEquals("Sunny", home.weather.condition)
    }


    @Test
    fun `loadHome with no logged in user sets Error`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadHome(null, null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertTrue((state as HomeUiState.Error).message.contains("User not logged in"))
    }

    @Test
    fun `loadHome with no locations sets Error`() = runTest {
        coEvery { locationRepository.getLocations() } returns emptyList()

        viewModel.loadHome(null, null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
        assertTrue((state as HomeUiState.Error).message.contains("No locations available"))
    }

    @Test
    fun `loadHome when locationRepository throws sets Error`() = runTest {
        coEvery { locationRepository.getLocations() } throws RuntimeException("network")

        viewModel.loadHome(null, null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
    }

    @Test
    fun `loadHome when weatherRepository throws sets Error`() = runTest {
        coEvery { locationRepository.getLocations() } returns listOf(fakeLocation())
        coEvery { weatherRepository.getWeather(any(), any(), any()) } throws
                RuntimeException("weather down")

        viewModel.loadHome(null, null)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is HomeUiState.Error)
    }


    @Test
    fun `loadHome uses onboarding prefs to rank playlists`() = runTest {
        every { onboardingPrefs.musicGenres } returns flowOf(setOf("Rock"))
        stubHappyPath(userGenres = setOf("Rock"))

        viewModel.loadHome(null, null)
        advanceUntilIdle()

        val home = (viewModel.uiState.value as HomeUiState.Success).home
        // The Rock playlist should be ranked first
        assertEquals("Rock", home.playlists.first().genre)
    }


    private fun stubHappyPath(userGenres: Set<String> = emptySet()) {
        every { onboardingPrefs.musicGenres } returns flowOf(userGenres)
        coEvery { locationRepository.getLocations() } returns listOf(fakeLocation())
        coEvery { weatherRepository.getWeather(any(), any(), any()) } returns fakeWeatherData()
        coEvery { playlistRepository.getPlaylists() } returns listOf(
            fakePlaylist("rock-1", genre = "Rock", likes = 50),
            fakePlaylist("pop-1", genre = "Pop", likes = 100),
            fakePlaylist("jazz-1", genre = "Jazz", likes = 80)
        )
    }

    private fun fakeLocation() = LocationResponse(
        id = "loc-1",
        name = "Sarajevo",
        description = "Capital",
        latitude = 43.8563,
        longitude = 18.4131
    )

    private fun fakeWeatherData() = WeatherData(
        weather = Weather(
            location = "Sarajevo",
            temperature = "20°C",
            condition = "Sunny",
            feelsLike = "20°C",
            humidity = "50%",
            wind = "5 km/h",
            uvIndex = "3",
            airQuality = "Good",
            icon = 0
        ),
        hourly = emptyList(),
        daily = listOf(
            Forecast(time = "Mon", icon = 0, temperature = "20°C")
        )
    )

    private fun fakePlaylist(
        id: String,
        genre: String,
        likes: Int
    ) = Playlist(
        id = id,
        title = "Title $id",
        genre = genre,
        mood = "Chill",
        albumImageUrl = null,
        weather = "Sunny",
        temperature = "20",
        location = "Sarajevo",
        songs = emptyList(),
        likes = likes,
        spotifyUrl = null,
        youtubeUrl = null,
        bestFor = emptyList()
    )
}