package com.emirgasic.forecastfm.feature.music.playlist

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.utils.YouTubeMapper
import com.emirgasic.forecastfm.data.model.Music
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.network.youtube.YouTubeApi
import com.emirgasic.forecastfm.network.youtube.YouTubeEnrichedItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
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
class PlaylistViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var youTubeApi: YouTubeApi
    private lateinit var viewModel: PlaylistViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        playlistRepository = mockk(relaxed = true)
        youTubeApi = mockk(relaxed = true)

        every { tokenManager.getUserId() } returns flowOf("user-123")

        viewModel = PlaylistViewModel(
            tokenManager = tokenManager,
            playlistRepository = playlistRepository,
            youTubeApi = youTubeApi
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- loadPlaylist ----------

    @Test
    fun `initial state is Loading`() {
        assertEquals(PlaylistUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadPlaylist success sets Success state with playlist`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns
                playlist("pl-1", youtubeUrl = null)
        coEvery { playlistRepository.getPlaylists() } returns emptyList()
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns emptyList()

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PlaylistUiState.Success)
        assertEquals("pl-1", (state as PlaylistUiState.Success).playlist.id)
    }

    @Test
    fun `loadPlaylist with repository exception sets Error`() = runTest {
        coEvery { playlistRepository.getPlaylist(any()) } throws
                RuntimeException("network down")

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is PlaylistUiState.Error)
        assertEquals("network down", (state as PlaylistUiState.Error).message)
    }

    @Test
    fun `loadPlaylist with no youtube url does not call youTubeApi`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns
                playlist("pl-1", youtubeUrl = null)
        coEvery { playlistRepository.getPlaylists() } returns emptyList()
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns emptyList()

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        coVerify(exactly = 0) { youTubeApi.getPlaylistItems(any()) }
    }

    @Test
    fun `loadPlaylist with youtube url fetches items and maps songs`() = runTest {
        mockkObject(YouTubeMapper)
        val mappedSongs = listOf(music("s1"), music("s2"))
        every { YouTubeMapper.playlistItemsToMusic(any()) } returns mappedSongs

        coEvery { playlistRepository.getPlaylist("pl-1") } returns
                playlist("pl-1", youtubeUrl = "https://www.youtube.com/watch?v=x&list=PLxyz")
        coEvery { youTubeApi.getPlaylistItems("PLxyz") } returns
                listOf(fakeItem("s1"), fakeItem("s2"))
        coEvery { playlistRepository.getPlaylists() } returns emptyList()
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns emptyList()

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        val playlist = (viewModel.uiState.value as PlaylistUiState.Success).playlist
        assertEquals(2, playlist.songs.size)

        unmockkObject(YouTubeMapper)
    }

    @Test
    fun `loadPlaylist with youtube fetch failure falls back to original songs`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns
                playlist("pl-1", youtubeUrl = "https://www.youtube.com/watch?v=x&list=PLxyz")
        coEvery { youTubeApi.getPlaylistItems(any()) } throws RuntimeException("youtube down")
        coEvery { playlistRepository.getPlaylists() } returns emptyList()
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns emptyList()

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is PlaylistUiState.Success)
    }

    @Test
    fun `loadPlaylist updates album image when youtube provides thumbnail and imageUrl is null`() = runTest {
        mockkObject(YouTubeMapper)
        every { YouTubeMapper.playlistItemsToMusic(any()) } returns emptyList()

        coEvery { playlistRepository.getPlaylist("pl-1") } returns
                playlist(
                    "pl-1",
                    youtubeUrl = "https://www.youtube.com/watch?v=x&list=PLxyz",
                    albumImageUrl = null
                )
        coEvery { youTubeApi.getPlaylistItems(any()) } returns
                listOf(fakeItem("s1", thumbnailUrl = "https://img.com/thumb.jpg"))
        coEvery { playlistRepository.getPlaylists() } returns emptyList()
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns emptyList()

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        val playlist = (viewModel.uiState.value as PlaylistUiState.Success).playlist
        assertEquals("https://img.com/thumb.jpg", playlist.albumImageUrl)
        coVerify(exactly = 1) {
            playlistRepository.updatePlaylistImage("pl-1", "https://img.com/thumb.jpg")
        }

        unmockkObject(YouTubeMapper)
    }

    // ---------- similar playlists ----------

    @Test
    fun `loadPlaylist populates similar playlists sorted by match score`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns
                playlist("pl-1", genre = "Rock", mood = "Chill", weather = "Sunny", location = "Sarajevo")
        coEvery { playlistRepository.getPlaylists() } returns listOf(
            playlist("pl-1"),
            playlist("pl-2", genre = "Rock", mood = "Chill", weather = "Sunny", location = "Sarajevo"),
            playlist("pl-3", genre = "Rock", mood = "Chill"),
            playlist("pl-4", genre = "Pop")
        )
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns emptyList()

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        val similar = viewModel.similarPlaylists.value
        assertEquals(3, similar.size)
        assertEquals("pl-2", similar[0].id)
        assertEquals("pl-3", similar[1].id)
    }

    @Test
    fun `loadPlaylist similar playlists excludes current playlist`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns playlist("pl-1")
        coEvery { playlistRepository.getPlaylists() } returns listOf(
            playlist("pl-1"),
            playlist("pl-2")
        )
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns emptyList()

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        assertTrue(viewModel.similarPlaylists.value.none { it.id == "pl-1" })
    }

    @Test
    fun `loadPlaylist similar playlists returns at most 3`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns playlist("pl-1")
        coEvery { playlistRepository.getPlaylists() } returns
                (1..10).map { playlist("pl-$it") }
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns emptyList()

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        assertEquals(3, viewModel.similarPlaylists.value.size)
    }

    // ---------- favorite initial state ----------

    @Test
    fun `loadPlaylist sets isFavorite true when playlist is in favorites`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns playlist("pl-1")
        coEvery { playlistRepository.getPlaylists() } returns emptyList()
        coEvery { playlistRepository.getFavoritePlaylistIds("user-123") } returns
                listOf("pl-1", "pl-2")

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        assertTrue(viewModel.isFavorite.value)
    }

    @Test
    fun `loadPlaylist sets isFavorite false when playlist is not in favorites`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns playlist("pl-1")
        coEvery { playlistRepository.getPlaylists() } returns emptyList()
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns listOf("pl-2")

        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()

        assertFalse(viewModel.isFavorite.value)
    }

    // ---------- toggleFavorite ----------

    @Test
    fun `toggleFavorite on non-favorite calls favoritePlaylist and sets true`() = runTest {
        viewModel.toggleFavorite("pl-1")
        advanceUntilIdle()

        coVerify(exactly = 1) { playlistRepository.favoritePlaylist("user-123", "pl-1") }
        assertTrue(viewModel.isFavorite.value)
    }

    @Test
    fun `toggleFavorite when already favorite calls unfavoritePlaylist and sets false`() = runTest {
        coEvery { playlistRepository.getPlaylist("pl-1") } returns playlist("pl-1")
        coEvery { playlistRepository.getPlaylists() } returns emptyList()
        coEvery { playlistRepository.getFavoritePlaylistIds(any()) } returns listOf("pl-1")
        viewModel.loadPlaylist("pl-1")
        advanceUntilIdle()
        assertTrue(viewModel.isFavorite.value)

        viewModel.toggleFavorite("pl-1")
        advanceUntilIdle()

        coVerify(exactly = 1) { playlistRepository.unfavoritePlaylist("user-123", "pl-1") }
        assertFalse(viewModel.isFavorite.value)
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

        assertFalse(viewModel.isFavorite.value)
    }

    // ---------- helpers ----------

    private fun playlist(
        id: String,
        genre: String = "Rock",
        mood: String = "Chill",
        weather: String = "Sunny",
        location: String = "Sarajevo",
        albumImageUrl: String? = null,
        youtubeUrl: String? = null,
        songs: List<Music> = emptyList()
    ) = Playlist(
        id = id,
        title = "Title $id",
        genre = genre,
        mood = mood,
        albumImageUrl = albumImageUrl,
        weather = weather,
        temperature = "20",
        location = location,
        songs = songs,
        likes = 0,
        spotifyUrl = null,
        youtubeUrl = youtubeUrl,
        bestFor = emptyList()
    )

    private fun music(id: String) = Music(
        id = id,
        title = "Song $id",
        artist = "Artist",
        duration = 180,
        albumImageUrl = null
    )

    private fun fakeItem(
        videoId: String,
        thumbnailUrl: String = "https://thumb.com/$videoId.jpg"
    ) = YouTubeEnrichedItem(
        videoId = videoId,
        title = "Title",
        artist = "Artist",
        duration = 180,
        thumbnailUrl = thumbnailUrl
    )
}