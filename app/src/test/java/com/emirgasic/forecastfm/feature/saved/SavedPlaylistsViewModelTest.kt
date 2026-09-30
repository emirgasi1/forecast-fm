package com.emirgasic.forecastfm.feature.saved

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SavedPlaylistsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var viewModel: SavedPlaylistsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        playlistRepository = mockk(relaxed = true)
        every { tokenManager.getUserId() } returns flowOf("user-123")
        viewModel = SavedPlaylistsViewModel(
            tokenManager = tokenManager,
            playlistRepository = playlistRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial savedPlaylists is empty`() {
        assertTrue(viewModel.savedPlaylists.value.isEmpty())
    }

    @Test
    fun `initial isLoading is false`() {
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- loadSavedPlaylists ----------

    @Test
    fun `loadSavedPlaylists success populates savedPlaylists`() = runTest {
        coEvery { playlistRepository.getFavoritePlaylists("user-123") } returns
                listOf(fakePlaylist("p1"), fakePlaylist("p2"))

        viewModel.loadSavedPlaylists()
        advanceUntilIdle()

        assertEquals(2, viewModel.savedPlaylists.value.size)
        assertEquals(setOf("p1", "p2"), viewModel.favoritePlaylistIds.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadSavedPlaylists with no user does not call repository`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadSavedPlaylists()
        advanceUntilIdle()

        coVerify(exactly = 0) { playlistRepository.getFavoritePlaylists(any()) }
        assertTrue(viewModel.savedPlaylists.value.isEmpty())
    }

    @Test
    fun `loadSavedPlaylists with repository exception leaves list empty`() = runTest {
        coEvery { playlistRepository.getFavoritePlaylists(any()) } throws
                RuntimeException("api down")

        viewModel.loadSavedPlaylists()
        advanceUntilIdle()

        assertTrue(viewModel.savedPlaylists.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- unsavePlaylist ----------

    @Test
    fun `unsavePlaylist removes from list and id set`() = runTest {
        coEvery { playlistRepository.getFavoritePlaylists(any()) } returns
                listOf(fakePlaylist("p1"), fakePlaylist("p2"))
        viewModel.loadSavedPlaylists()
        advanceUntilIdle()

        viewModel.unsavePlaylist("p1")
        advanceUntilIdle()

        assertEquals(1, viewModel.savedPlaylists.value.size)
        assertEquals("p2", viewModel.savedPlaylists.value.first().id)
        assertFalse("p1" in viewModel.favoritePlaylistIds.value)
        coVerify(exactly = 1) { playlistRepository.unfavoritePlaylist("user-123", "p1") }
    }

    @Test
    fun `unsavePlaylist with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.unsavePlaylist("p1")
        advanceUntilIdle()

        coVerify(exactly = 0) { playlistRepository.unfavoritePlaylist(any(), any()) }
    }

    @Test
    fun `unsavePlaylist with repository exception leaves list unchanged`() = runTest {
        coEvery { playlistRepository.getFavoritePlaylists(any()) } returns
                listOf(fakePlaylist("p1"))
        viewModel.loadSavedPlaylists()
        advanceUntilIdle()

        coEvery { playlistRepository.unfavoritePlaylist(any(), any()) } throws
                RuntimeException("api down")

        viewModel.unsavePlaylist("p1")
        advanceUntilIdle()

        assertEquals(1, viewModel.savedPlaylists.value.size)
    }

    // ---------- helpers ----------

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
}