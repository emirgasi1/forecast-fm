package com.emirgasic.forecastfm.feature.music.musichistory

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.MusicHistory
import com.emirgasic.forecastfm.data.repository.MusicHistoryRepository
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
class MusicHistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var musicHistoryRepository: MusicHistoryRepository
    private lateinit var viewModel: MusicHistoryViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        musicHistoryRepository = mockk(relaxed = true)
        every { tokenManager.getUserId() } returns flowOf("user-123")
        viewModel = MusicHistoryViewModel(
            tokenManager = tokenManager,
            musicHistoryRepository = musicHistoryRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `initial history is empty`() {
        assertTrue(viewModel.history.value.isEmpty())
    }

    @Test
    fun `initial isLoading is false`() {
        assertFalse(viewModel.isLoading.value)
    }


    @Test
    fun `loadHistory success populates history`() = runTest {
        coEvery { musicHistoryRepository.getMusicHistory("user-123") } returns
                listOf(fakeHistory("h1"), fakeHistory("h2"))

        viewModel.loadHistory()
        advanceUntilIdle()

        assertEquals(2, viewModel.history.value.size)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadHistory sets isLoading false after completion`() = runTest {
        coEvery { musicHistoryRepository.getMusicHistory(any()) } returns emptyList()

        viewModel.loadHistory()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadHistory with no user does not call repository`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadHistory()
        advanceUntilIdle()

        coVerify(exactly = 0) { musicHistoryRepository.getMusicHistory(any()) }
        assertTrue(viewModel.history.value.isEmpty())
    }

    @Test
    fun `loadHistory with repository exception does not crash`() = runTest {
        coEvery { musicHistoryRepository.getMusicHistory(any()) } throws
                RuntimeException("api down")

        viewModel.loadHistory()
        advanceUntilIdle()

        assertTrue(viewModel.history.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadHistory can be called twice and reloads data`() = runTest {
        coEvery { musicHistoryRepository.getMusicHistory(any()) } returns
                listOf(fakeHistory("h1"))

        viewModel.loadHistory()
        advanceUntilIdle()
        assertEquals(1, viewModel.history.value.size)

        coEvery { musicHistoryRepository.getMusicHistory(any()) } returns
                listOf(fakeHistory("h1"), fakeHistory("h2"))
        viewModel.loadHistory()
        advanceUntilIdle()

        assertEquals(2, viewModel.history.value.size)
        coVerify(exactly = 2) { musicHistoryRepository.getMusicHistory(any()) }
    }


    @Test
    fun `openPlaylist records history and refreshes`() = runTest {
        coEvery { musicHistoryRepository.getMusicHistory(any()) } returns
                listOf(fakeHistory("h1"))

        var readyUrl: String? = null
        viewModel.openPlaylist("pl-1", "https://youtube.com/watch?v=abc") {
            readyUrl = it
        }
        advanceUntilIdle()

        coVerify(exactly = 1) {
            musicHistoryRepository.addHistory("user-123", "pl-1")
        }
        coVerify(atLeast = 1) { musicHistoryRepository.getMusicHistory("user-123") }
        assertEquals("https://youtube.com/watch?v=abc", readyUrl)
    }

    @Test
    fun `openPlaylist with null url does not invoke callback`() = runTest {
        var callbackInvoked = false

        viewModel.openPlaylist("pl-1", null) { callbackInvoked = true }
        advanceUntilIdle()

        assertFalse(callbackInvoked)
        coVerify(exactly = 1) {
            musicHistoryRepository.addHistory("user-123", "pl-1")
        }
    }

    @Test
    fun `openPlaylist with blank url does not invoke callback`() = runTest {
        var callbackInvoked = false

        viewModel.openPlaylist("pl-1", "   ") { callbackInvoked = true }
        advanceUntilIdle()

        assertFalse(callbackInvoked)
    }

    @Test
    fun `openPlaylist with no user does not call repository but still invokes callback`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        var readyUrl: String? = null
        viewModel.openPlaylist("pl-1", "https://example.com") { readyUrl = it }
        advanceUntilIdle()

        coVerify(exactly = 0) { musicHistoryRepository.addHistory(any(), any()) }
        assertEquals("https://example.com", readyUrl)
    }

    @Test
    fun `openPlaylist with repository exception does not crash`() = runTest {
        coEvery { musicHistoryRepository.addHistory(any(), any()) } throws
                RuntimeException("api down")

        var callbackInvoked = false
        viewModel.openPlaylist("pl-1", "https://example.com") { callbackInvoked = true }
        advanceUntilIdle()

        assertFalse(callbackInvoked)
    }


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