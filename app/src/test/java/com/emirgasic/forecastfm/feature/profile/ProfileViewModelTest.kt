package com.emirgasic.forecastfm.feature.profile

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Profile
import com.emirgasic.forecastfm.data.repository.ProfileRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var profileRepository: ProfileRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        profileRepository = mockk()
        every { tokenManager.getUserId() } returns flowOf("user-123")
        viewModel = ProfileViewModel(
            tokenManager = tokenManager,
            profileRepository = profileRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `initial state is Loading`() {
        assertEquals(ProfileUiState.Loading, viewModel.uiState.value)
    }


    @Test
    fun `loadProfile success sets Success with profile`() = runTest {
        coEvery { profileRepository.getProfile("user-123") } returns fakeProfile()

        viewModel.loadProfile()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue("Expected Success, got $state", state is ProfileUiState.Success)
        val profile = (state as ProfileUiState.Success).profile
        assertEquals("emir", profile.username)
        assertEquals("Hello", profile.bio)
        assertEquals(10, profile.likes)
        assertEquals(5, profile.saved)
        assertEquals(3, profile.posts)
    }

    @Test
    fun `loadProfile passes userId from tokenManager to repository`() = runTest {
        coEvery { profileRepository.getProfile(any()) } returns fakeProfile()

        viewModel.loadProfile()
        advanceUntilIdle()

        coVerify(exactly = 1) { profileRepository.getProfile("user-123") }
    }


    @Test
    fun `loadProfile with no logged in user sets Error`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadProfile()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProfileUiState.Error)
        assertEquals("User not logged in", (state as ProfileUiState.Error).message)
    }

    @Test
    fun `loadProfile with repository exception sets Error with message`() = runTest {
        coEvery { profileRepository.getProfile(any()) } throws RuntimeException("network down")

        viewModel.loadProfile()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProfileUiState.Error)
        assertEquals("network down", (state as ProfileUiState.Error).message)
    }

    @Test
    fun `loadProfile with exception with null message uses fallback`() = runTest {
        coEvery { profileRepository.getProfile(any()) } throws RuntimeException()

        viewModel.loadProfile()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ProfileUiState.Error)
        assertEquals("Failed to load profile", (state as ProfileUiState.Error).message)
    }


    @Test
    fun `retry after error succeeds and replaces Error with Success`() = runTest {
        coEvery { profileRepository.getProfile(any()) } throws RuntimeException("first try fails")

        viewModel.loadProfile()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ProfileUiState.Error)

        coEvery { profileRepository.getProfile(any()) } returns fakeProfile()
        viewModel.loadProfile()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is ProfileUiState.Success)
    }

    @Test
    fun `second loadProfile resets to Loading then sets Success`() = runTest {
        coEvery { profileRepository.getProfile(any()) } returns fakeProfile()

        viewModel.loadProfile()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ProfileUiState.Success)

        viewModel.loadProfile()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value is ProfileUiState.Success)

        coVerify(exactly = 2) { profileRepository.getProfile(any()) }
    }


    private fun fakeProfile() = Profile(
        username = "emir",
        bio = "Hello",
        profileImage = "https://example.com/pic.jpg",
        likes = 10,
        saved = 5,
        posts = 3,
        favoritePlaylists = emptyList(),
        profilePosts = emptyList()
    )
}