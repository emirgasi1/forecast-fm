package com.emirgasic.forecastfm.feature.admin

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.theme.AppTheme
import com.emirgasic.forecastfm.core.theme.ThemeManager
import com.emirgasic.forecastfm.network.ApiConfig
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: AdminViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk(relaxed = true)

        // Mock the singletons
        mockkObject(ThemeManager)
        mockkObject(ApiConfig)

        // Default stubs
        every { ThemeManager.selectedTheme } returns MutableStateFlow(AppTheme.AUTO)
        every { ThemeManager.currentHour() } returns 10
        every { ThemeManager.setTheme(any()) } returns Unit

        every { ApiConfig.current() } returns "https://forecastfm-backend.onrender.com"
        every { ApiConfig.setBaseUrl(any()) } returns Unit
        every { ApiConfig.reset() } returns Unit

        viewModel = AdminViewModel(tokenManager = tokenManager)
    }

    @After
    fun tearDown() {
        unmockkObject(ThemeManager)
        unmockkObject(ApiConfig)
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial theme reads from ThemeManager`() {
        assertEquals(AppTheme.AUTO, viewModel.currentTheme.value)
    }

    @Test
    fun `initial apiUrl reads from ApiConfig`() {
        assertEquals("https://forecastfm-backend.onrender.com", viewModel.currentApiUrl.value)
    }

    @Test
    fun `initial userId is NA`() {
        assertEquals("N/A", viewModel.userId.value)
    }

    @Test
    fun `initial userEmail is NA`() {
        assertEquals("N/A", viewModel.userEmail.value)
    }

    @Test
    fun `initial backendStatus is Not checked`() {
        assertEquals("Not checked", viewModel.backendStatus.value)
    }

    // ---------- loadUserInfo ----------

    @Test
    fun `loadUserInfo populates userId and userEmail`() = runTest {
        every { tokenManager.getUserId() } returns MutableStateFlow("user-123")
        every { tokenManager.getUserEmail() } returns MutableStateFlow("emir@test.com")

        viewModel.loadUserInfo()
        advanceUntilIdle()

        assertEquals("user-123", viewModel.userId.value)
        assertEquals("emir@test.com", viewModel.userEmail.value)
    }

    @Test
    fun `loadUserInfo with null userId uses NA`() = runTest {
        every { tokenManager.getUserId() } returns MutableStateFlow(null)
        every { tokenManager.getUserEmail() } returns MutableStateFlow("emir@test.com")

        viewModel.loadUserInfo()
        advanceUntilIdle()

        assertEquals("N/A", viewModel.userId.value)
    }

    @Test
    fun `loadUserInfo with null email uses NA`() = runTest {
        every { tokenManager.getUserId() } returns MutableStateFlow("user-123")
        every { tokenManager.getUserEmail() } returns MutableStateFlow(null)

        viewModel.loadUserInfo()
        advanceUntilIdle()

        assertEquals("N/A", viewModel.userEmail.value)
    }

    // ---------- setTheme ----------

    @Test
    fun `setTheme calls ThemeManager and updates state`() = runTest {
        viewModel.setTheme(AppTheme.NIGHT)

        verify(exactly = 1) { ThemeManager.setTheme(AppTheme.NIGHT) }
        assertEquals(AppTheme.NIGHT, viewModel.currentTheme.value)
    }

    // ---------- setApiUrl ----------

    @Test
    fun `setApiUrl calls ApiConfig and updates state from current`() = runTest {
        every { ApiConfig.current() } returns "https://new-url.com"

        viewModel.setApiUrl("https://new-url.com")

        verify(exactly = 1) { ApiConfig.setBaseUrl("https://new-url.com") }
        assertEquals("https://new-url.com", viewModel.currentApiUrl.value)
    }

    // ---------- resetApiUrl ----------

    @Test
    fun `resetApiUrl calls ApiConfig reset and updates state`() = runTest {
        every { ApiConfig.current() } returns "https://forecastfm-backend.onrender.com"

        viewModel.resetApiUrl()

        verify(exactly = 1) { ApiConfig.reset() }
        assertEquals("https://forecastfm-backend.onrender.com", viewModel.currentApiUrl.value)
    }

    // ---------- logout ----------

    @Test
    fun `logout calls tokenManager clearTokens`() = runTest {
        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 1) { tokenManager.clearTokens() }
    }

    @Test
    fun `logout does not crash when clearTokens throws`() = runTest {
        io.mockk.coEvery { tokenManager.clearTokens() } throws RuntimeException("storage fail")

        viewModel.logout()
        advanceUntilIdle()

        // If we reached here without runTest reporting an uncaught exception, the ViewModel handled it
        coVerify(exactly = 1) { tokenManager.clearTokens() }
    }

    // ---------- currentHour ----------

    @Test
    fun `currentHour reads from ThemeManager`() {
        assertEquals(10, viewModel.currentHour.value)
    }
}