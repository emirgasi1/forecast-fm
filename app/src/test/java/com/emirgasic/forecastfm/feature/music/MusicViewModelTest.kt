package com.emirgasic.forecastfm.feature.music

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.LocationRepository
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.data.repository.WeatherRepository
import com.emirgasic.forecastfm.data.repository.YouTubeRepository
import com.emirgasic.forecastfm.network.youtube.YouTubeVideo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MusicViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var onboardingPrefs: OnboardingPreferences
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var locationRepository: LocationRepository
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var musicHistoryRepository: MusicHistoryRepository
    private lateinit var userRepository: UserRepository
    private lateinit var youTubeRepository: YouTubeRepository

    private lateinit var viewModel: MusicViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        onboardingPrefs = mockk(relaxed = true)
        playlistRepository = mockk(relaxed = true)
        locationRepository = mockk(relaxed = true)
        weatherRepository = mockk(relaxed = true)
        musicHistoryRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        youTubeRepository = mockk()

        every { tokenManager.getUserId() } returns flowOf("user-123")
        every { onboardingPrefs.musicGenres } returns flowOf(emptySet())
        every { onboardingPrefs.moods } returns flowOf(emptySet())
        every { onboardingPrefs.weatherPrefs } returns flowOf(emptySet())

        viewModel = MusicViewModel(
            tokenManager = tokenManager,
            onboardingPrefs = onboardingPrefs,
            playlistRepository = playlistRepository,
            locationRepository = locationRepository,
            weatherRepository = weatherRepository,
            musicHistoryRepository = musicHistoryRepository,
            userRepository = userRepository,
            youTubeRepository = youTubeRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `updateSearch updates search state`() = runTest {
        viewModel.updateSearch("rock")
        assertEquals("rock", viewModel.search.value)
    }

    @Test
    fun `updateSearch with blank input clears tracks and error`() = runTest {
        viewModel.updateSearch("")
        advanceUntilIdle()

        assertTrue(viewModel.tracks.value.isEmpty())
        assertNull(viewModel.error.value)
    }

    @Test
    fun `updateSearch with non-blank triggers search after debounce delay`() = runTest {
        coEvery { youTubeRepository.searchVideos("rock") } returns
                listOf(fakeVideo("v1"), fakeVideo("v2"))

        viewModel.updateSearch("rock")
        coVerify(exactly = 0) { youTubeRepository.searchVideos(any()) }

        advanceTimeBy(350)
        advanceUntilIdle()

        coVerify(exactly = 1) { youTubeRepository.searchVideos("rock") }
    }

    @Test
    fun `rapid updateSearch calls only search once for the latest query`() = runTest {
        coEvery { youTubeRepository.searchVideos("r") } returns emptyList()
        coEvery { youTubeRepository.searchVideos("ro") } returns emptyList()
        coEvery { youTubeRepository.searchVideos("roc") } returns emptyList()
        coEvery { youTubeRepository.searchVideos("rock") } returns emptyList()

        viewModel.updateSearch("r")
        viewModel.updateSearch("ro")
        viewModel.updateSearch("roc")
        viewModel.updateSearch("rock")

        advanceTimeBy(350)
        advanceUntilIdle()

        // Only the last one should have fired
        coVerify(exactly = 1) { youTubeRepository.searchVideos("rock") }
    }

    @Test
    fun `updateSearch with blank does not call repository`() = runTest {
        viewModel.updateSearch("")
        advanceUntilIdle()

        coVerify(exactly = 0) { youTubeRepository.searchVideos(any()) }
    }


    @Test
    fun `searchYouTube success sets tracks and clears error`() = runTest {
        coEvery { youTubeRepository.searchVideos("rock") } returns
                listOf(fakeVideo("v1"), fakeVideo("v2"))

        viewModel.searchYouTube("rock")
        advanceUntilIdle()

        assertEquals(2, viewModel.tracks.value.size)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `searchYouTube empty results sets error`() = runTest {
        coEvery { youTubeRepository.searchVideos("nothing") } returns emptyList()

        viewModel.searchYouTube("nothing")
        advanceUntilIdle()

        assertTrue(viewModel.tracks.value.isEmpty())
        assertEquals("No results found", viewModel.error.value)
    }

    @Test
    fun `searchYouTube quota error sets specific message`() = runTest {
        coEvery { youTubeRepository.searchVideos(any()) } throws
                RuntimeException("HTTP 429 Too Many Requests")

        viewModel.searchYouTube("rock")
        advanceUntilIdle()

        assertEquals("API quota exceeded. Please try again tomorrow.", viewModel.error.value)
    }

    @Test
    fun `searchYouTube generic error sets raw message`() = runTest {
        coEvery { youTubeRepository.searchVideos(any()) } throws
                RuntimeException("network down")

        viewModel.searchYouTube("rock")
        advanceUntilIdle()

        assertEquals("network down", viewModel.error.value)
    }

    @Test
    fun `searchYouTube sets isLoading false after error`() = runTest {
        coEvery { youTubeRepository.searchVideos(any()) } throws RuntimeException("boom")

        viewModel.searchYouTube("rock")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }


    @Test
    fun `toggleFavorite adds id when not favorite`() = runTest {
        viewModel.toggleFavorite("pl-1")
        advanceUntilIdle()

        assertTrue("pl-1" in viewModel.favoritePlaylistIds.value)
        coVerify(exactly = 1) { playlistRepository.favoritePlaylist("user-123", "pl-1") }
    }

    @Test
    fun `toggleFavorite removes id when already favorite`() = runTest {
        // First favorite it
        viewModel.toggleFavorite("pl-1")
        advanceUntilIdle()
        assertTrue("pl-1" in viewModel.favoritePlaylistIds.value)

        // Now unfavorite
        viewModel.toggleFavorite("pl-1")
        advanceUntilIdle()

        assertFalse("pl-1" in viewModel.favoritePlaylistIds.value)
        coVerify(exactly = 1) { playlistRepository.unfavoritePlaylist("user-123", "pl-1") }
    }

    @Test
    fun `toggleFavorite with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.toggleFavorite("pl-1")
        advanceUntilIdle()

        coVerify(exactly = 0) { playlistRepository.favoritePlaylist(any(), any()) }
        coVerify(exactly = 0) { playlistRepository.unfavoritePlaylist(any(), any()) }
    }

    @Test
    fun `toggleFavorite with repository exception leaves state unchanged`() = runTest {
        coEvery { playlistRepository.favoritePlaylist(any(), any()) } throws
                RuntimeException("api down")

        viewModel.toggleFavorite("pl-1")
        advanceUntilIdle()

        assertFalse("pl-1" in viewModel.favoritePlaylistIds.value)
    }


    @Test
    fun `openPlaylist adds history entry`() = runTest {
        viewModel.openPlaylist(fakePlaylist("pl-1"))
        advanceUntilIdle()

        coVerify(exactly = 1) {
            musicHistoryRepository.addHistory("user-123", "pl-1")
        }
    }

    @Test
    fun `openPlaylist with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.openPlaylist(fakePlaylist("pl-1"))
        advanceUntilIdle()

        coVerify(exactly = 0) { musicHistoryRepository.addHistory(any(), any()) }
    }


    @Test
    fun `loadMusicHistory success populates musicHistory`() = runTest {
        coEvery { musicHistoryRepository.getMusicHistory("user-123") } returns
                listOf(fakeHistory("h1"), fakeHistory("h2"))

        viewModel.loadMusicHistory()
        advanceUntilIdle()

        assertEquals(2, viewModel.musicHistory.value.size)
    }

    @Test
    fun `loadMusicHistory with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadMusicHistory()
        advanceUntilIdle()

        coVerify(exactly = 0) { musicHistoryRepository.getMusicHistory(any()) }
    }

    @Test
    fun `loadMusicHistory with repository exception does not crash`() = runTest {
        coEvery { musicHistoryRepository.getMusicHistory(any()) } throws
                RuntimeException("api down")

        viewModel.loadMusicHistory()
        advanceUntilIdle()

        assertTrue(viewModel.musicHistory.value.isEmpty())
    }


    @Test
    fun `selectGenre updates selectedGenre state`() = runTest {
        viewModel.selectGenre("Rock")
        assertEquals("Rock", viewModel.selectedGenre.value)
    }


    @Test
    fun `loadUserPreferences reads from onboardingPrefs`() = runTest {
        viewModel.loadUserPreferences()
        advanceUntilIdle()

        verify(atLeast = 1) { onboardingPrefs.musicGenres }
        verify(atLeast = 1) { onboardingPrefs.moods }
        verify(atLeast = 1) { onboardingPrefs.weatherPrefs }
    }


    private fun fakeVideo(id: String): YouTubeVideo {

        return mockk(relaxed = true)
    }

    private fun fakePlaylist(id: String) = Playlist(
        id = id,
        title = "Title $id",
        genre = "Rock",
        mood = "Chill",
        albumImageUrl = null,
        weather = "Sunny",
        temperature = "20",
        location = "Sarajevo",
        songs = emptyList(),
        likes = 0,
        spotifyUrl = null,
        youtubeUrl = null,
        bestFor = emptyList()
    )

    private fun fakeHistory(id: String) = MusicHistory(
        id = id,
        section = "Today",
        title = "Title $id",
        weather = "Sunny",
        temperature = "20",
        location = "Sarajevo",
        time = "10:00 AM",
        weatherIcon = 0
    )
}