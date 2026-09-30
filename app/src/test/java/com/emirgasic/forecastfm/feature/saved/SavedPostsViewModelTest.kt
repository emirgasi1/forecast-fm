package com.emirgasic.forecastfm.feature.saved

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.SavedPostRepository
import com.emirgasic.forecastfm.network.post.PostResponse
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
class SavedPostsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var savedPostRepository: SavedPostRepository
    private lateinit var viewModel: SavedPostsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        savedPostRepository = mockk(relaxed = true)
        every { tokenManager.getUserId() } returns flowOf("user-123")
        viewModel = SavedPostsViewModel(
            tokenManager = tokenManager,
            savedPostRepository = savedPostRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial savedPosts is empty`() {
        assertTrue(viewModel.savedPosts.value.isEmpty())
    }

    @Test
    fun `initial isLoading is false`() {
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- loadSavedPosts ----------

    @Test
    fun `loadSavedPosts success populates savedPosts`() = runTest {
        coEvery { savedPostRepository.getSavedPosts("user-123") } returns
                listOf(fakePostResponse("p1"), fakePostResponse("p2"))

        viewModel.loadSavedPosts()
        advanceUntilIdle()

        assertEquals(2, viewModel.savedPosts.value.size)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadSavedPosts with no user does not call repository`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadSavedPosts()
        advanceUntilIdle()

        coVerify(exactly = 0) { savedPostRepository.getSavedPosts(any()) }
        assertTrue(viewModel.savedPosts.value.isEmpty())
    }

    @Test
    fun `loadSavedPosts with repository exception leaves list empty`() = runTest {
        coEvery { savedPostRepository.getSavedPosts(any()) } throws
                RuntimeException("api down")

        viewModel.loadSavedPosts()
        advanceUntilIdle()

        assertTrue(viewModel.savedPosts.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- unsavePost ----------

    @Test
    fun `unsavePost removes from list`() = runTest {
        coEvery { savedPostRepository.getSavedPosts(any()) } returns
                listOf(fakePostResponse("p1"), fakePostResponse("p2"))
        viewModel.loadSavedPosts()
        advanceUntilIdle()

        viewModel.unsavePost("p1")
        advanceUntilIdle()

        assertEquals(1, viewModel.savedPosts.value.size)
        assertEquals("p2", viewModel.savedPosts.value.first().id)
        coVerify(exactly = 1) { savedPostRepository.unsavePost("p1", "user-123") }
    }

    @Test
    fun `unsavePost with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.unsavePost("p1")
        advanceUntilIdle()

        coVerify(exactly = 0) { savedPostRepository.unsavePost(any(), any()) }
    }

    // ---------- helpers ----------

    private fun fakePostResponse(id: String) = PostResponse(
        id = id,
        userId = "user-123",
        imageUrl = null,
        caption = null,
        createdAt = "2026-01-01T10:00:00Z",
        likes = 0
    )
}