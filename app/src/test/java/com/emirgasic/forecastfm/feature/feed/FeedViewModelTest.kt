package com.emirgasic.forecastfm.feature.feed

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.FeedPost
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.model.Playlist
import com.emirgasic.forecastfm.data.model.User
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.repository.FeedRepository
import com.emirgasic.forecastfm.data.repository.LikeRepository
import com.emirgasic.forecastfm.data.repository.OutfitRepository
import com.emirgasic.forecastfm.data.repository.PlaylistRepository
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.data.repository.SavedPostRepository
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
class FeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var feedRepository: FeedRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var savedPostRepository: SavedPostRepository
    private lateinit var savedOutfitRepository: SavedOutfitRepository
    private lateinit var outfitRepository: OutfitRepository
    private lateinit var playlistRepository: PlaylistRepository

    private lateinit var viewModel: FeedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        feedRepository = mockk()
        likeRepository = mockk()
        savedPostRepository = mockk(relaxed = true)
        savedOutfitRepository = mockk(relaxed = true)
        outfitRepository = mockk()
        playlistRepository = mockk(relaxed = true)

        every { tokenManager.getUserId() } returns flowOf("user-123")

        viewModel = FeedViewModel(
            tokenManager = tokenManager,
            feedRepository = feedRepository,
            likeRepository = likeRepository,
            savedPostRepository = savedPostRepository,
            savedOutfitRepository = savedOutfitRepository,
            outfitRepository = outfitRepository,
            playlistRepository = playlistRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `initial state is Loading`() {
        assertEquals(FeedUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadFeed success sets Success with posts`() = runTest {
        coEvery { feedRepository.getPosts("user-123") } returns listOf(post("p1"))
        coEvery { likeRepository.isPostLiked("p1", "user-123") } returns false

        viewModel.loadFeed()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FeedUiState.Success)
        assertEquals(1, (state as FeedUiState.Success).posts.size)
    }

    @Test
    fun `loadFeed with empty posts returns Success empty`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns emptyList()

        viewModel.loadFeed()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FeedUiState.Success)
        assertTrue((state as FeedUiState.Success).posts.isEmpty())
    }

    @Test
    fun `loadFeed with no user sets Error`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadFeed()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FeedUiState.Error)
        assertEquals("User not logged in", (state as FeedUiState.Error).message)
    }

    @Test
    fun `loadFeed with repository exception sets Error`() = runTest {
        coEvery { feedRepository.getPosts(any()) } throws RuntimeException("network down")

        viewModel.loadFeed()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FeedUiState.Error)
        assertEquals("network down", (state as FeedUiState.Error).message)
    }

    @Test
    fun `loadFeed populates likedPosts from likeRepository`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns listOf(
            post("p1"), post("p2"), post("p3")
        )
        coEvery { likeRepository.isPostLiked("p1", "user-123") } returns true
        coEvery { likeRepository.isPostLiked("p2", "user-123") } returns false
        coEvery { likeRepository.isPostLiked("p3", "user-123") } returns true

        viewModel.loadFeed()
        advanceUntilIdle()

        assertEquals(setOf("p1", "p3"), viewModel.likedPosts.value)
    }

    @Test
    fun `loadFeed skips liked check exceptions for individual posts`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns listOf(
            post("p1"), post("p2")
        )
        coEvery { likeRepository.isPostLiked("p1", "user-123") } throws
                RuntimeException("api flake")
        coEvery { likeRepository.isPostLiked("p2", "user-123") } returns true

        viewModel.loadFeed()
        advanceUntilIdle()

        // p1 skipped due to exception, p2 included
        assertEquals(setOf("p2"), viewModel.likedPosts.value)
        assertTrue(viewModel.uiState.value is FeedUiState.Success)
    }


    @Test
    fun `toggleLike on unliked post adds to likedPosts and calls likePost`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns listOf(post("p1", likes = 10))
        coEvery { likeRepository.isPostLiked(any(), any()) } returns false
        coEvery { likeRepository.likePost(any(), any()) } returns Unit

        viewModel.loadFeed()
        advanceUntilIdle()

        viewModel.toggleLike("p1")
        advanceUntilIdle()

        assertTrue("p1" in viewModel.likedPosts.value)
        coVerify(exactly = 1) { likeRepository.likePost("p1", "user-123") }
    }

    @Test
    fun `toggleLike on liked post removes from likedPosts and calls unlikePost`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns listOf(post("p1", likes = 10))
        coEvery { likeRepository.isPostLiked("p1", any()) } returns true
        coEvery { likeRepository.unlikePost(any(), any()) } returns Unit

        viewModel.loadFeed()
        advanceUntilIdle()
        assertTrue("p1" in viewModel.likedPosts.value)

        viewModel.toggleLike("p1")
        advanceUntilIdle()

        assertFalse("p1" in viewModel.likedPosts.value)
        coVerify(exactly = 1) { likeRepository.unlikePost("p1", "user-123") }
    }

    @Test
    fun `toggleLike updates like count optimistically`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns listOf(post("p1", likes = 10))
        coEvery { likeRepository.isPostLiked(any(), any()) } returns false
        coEvery { likeRepository.likePost(any(), any()) } returns Unit

        viewModel.loadFeed()
        advanceUntilIdle()

        viewModel.toggleLike("p1")
        advanceUntilIdle()

        val state = viewModel.uiState.value as FeedUiState.Success
        assertEquals(11, state.posts.first().likes)
    }

    @Test
    fun `toggleLike like API failure reloads feed`() = runTest {
        var loadCount = 0
        coEvery { feedRepository.getPosts(any()) } coAnswers {
            loadCount++
            listOf(post("p1", likes = 10))
        }
        coEvery { likeRepository.isPostLiked(any(), any()) } returns false
        coEvery { likeRepository.likePost(any(), any()) } throws RuntimeException("api down")

        viewModel.loadFeed()
        advanceUntilIdle()
        assertEquals(1, loadCount)

        viewModel.toggleLike("p1")
        advanceUntilIdle()

        assertEquals(2, loadCount)
    }


    @Test
    fun `savePost calls repository and adds to savedPosts`() = runTest {
        viewModel.savePost("p1")
        advanceUntilIdle()

        coVerify(exactly = 1) { savedPostRepository.savePost("p1", "user-123") }
        assertTrue("p1" in viewModel.savedPosts.value)
    }

    @Test
    fun `savePost failure leaves savedPosts unchanged`() = runTest {
        coEvery { savedPostRepository.savePost(any(), any()) } throws RuntimeException("boom")

        viewModel.savePost("p1")
        advanceUntilIdle()

        assertFalse("p1" in viewModel.savedPosts.value)
    }

    @Test
    fun `savePost with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.savePost("p1")
        advanceUntilIdle()

        coVerify(exactly = 0) { savedPostRepository.savePost(any(), any()) }
    }


    @Test
    fun `savePlaylistFromPost favorites the post playlist`() = runTest {
        val playlist = Playlist(
            id = "pl-1", title = "T", genre = "g", mood = "m",
            albumImageUrl = null, weather = "Sunny", temperature = "20",
            location = "Sarajevo", songs = emptyList(), likes = 0,
            spotifyUrl = null, youtubeUrl = null, bestFor = emptyList()
        )
        coEvery { feedRepository.getPosts(any()) } returns
                listOf(post("p1", playlist = playlist))

        viewModel.loadFeed()
        advanceUntilIdle()

        viewModel.savePlaylistFromPost("p1")
        advanceUntilIdle()

        coVerify(exactly = 1) { playlistRepository.favoritePlaylist("user-123", "pl-1") }
    }

    @Test
    fun `savePlaylistFromPost with null playlist does nothing`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns listOf(post("p1", playlist = null))

        viewModel.loadFeed()
        advanceUntilIdle()

        viewModel.savePlaylistFromPost("p1")
        advanceUntilIdle()

        coVerify(exactly = 0) { playlistRepository.favoritePlaylist(any(), any()) }
    }

    @Test
    fun `savePlaylistFromPost with unknown post id does nothing`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns listOf(post("p1"))

        viewModel.loadFeed()
        advanceUntilIdle()

        viewModel.savePlaylistFromPost("nonexistent")
        advanceUntilIdle()

        coVerify(exactly = 0) { playlistRepository.favoritePlaylist(any(), any()) }
    }


    @Test
    fun `saveStyleFromPost saves first matching outfit`() = runTest {
        val outfit = outfit("outfit-1")
        coEvery { feedRepository.getPosts(any()) } returns listOf(post("p1"))
        coEvery { outfitRepository.getOutfitsByWeather(any()) } returns listOf(outfit)

        viewModel.loadFeed()
        advanceUntilIdle()

        viewModel.saveStyleFromPost("p1")
        advanceUntilIdle()

        coVerify(exactly = 1) { savedOutfitRepository.saveOutfit("user-123", "outfit-1") }
    }

    @Test
    fun `saveStyleFromPost with no matching outfits does nothing`() = runTest {
        coEvery { feedRepository.getPosts(any()) } returns listOf(post("p1"))
        coEvery { outfitRepository.getOutfitsByWeather(any()) } returns emptyList()

        viewModel.loadFeed()
        advanceUntilIdle()

        viewModel.saveStyleFromPost("p1")
        advanceUntilIdle()

        coVerify(exactly = 0) { savedOutfitRepository.saveOutfit(any(), any()) }
    }


    private fun post(
        id: String,
        likes: Int = 0,
        playlist: Playlist? = null
    ) = FeedPost(
        id = id,
        user = user(),
        image = "https://example.com/img.jpg",
        caption = "caption",
        weather = weather(),
        playlist = playlist,
        time = "1 Jan 2026",
        likes = likes,
        comments = 0
    )

    private fun user() = User(
        id = "user-123",
        username = "emir",
        bio = "",
        profileImage = "",
        favoriteLocation = "Sarajevo",
        likes = 0,
        posts = 0,
        saved = 0
    )

    private fun weather() = Weather(
        location = "Sarajevo",
        temperature = "20°C",
        condition = "Sunny",
        feelsLike = "20°C",
        humidity = "50%",
        wind = "5 km/h",
        uvIndex = "3",
        airQuality = "Good",
        icon = 0
    )

    private fun outfit(id: String) = Outfit(
        id = id,
        userId = "user-123",
        imageUrl = "",
        title = "T",
        weatherCondition = "Sunny",
        season = "Summer",
        likes = 0,
        storeName = "",
        storeAddress = "",
        price = "",
        storePhone = "",
        productUrl = "",
        createdAt = ""
    )
}