package com.emirgasic.forecastfm.feature.settings

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.core.navigation.Routes
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk(relaxed = true)
        viewModel = SettingsViewModel(tokenManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- accountOptions ----------

    @Test
    fun `accountOptions contains Edit Profile and Saved`() {
        val options = viewModel.accountOptions.value

        assertEquals(2, options.size)
        assertEquals("Edit Profile", options[0].title)
        assertEquals(Routes.EditProfile, options[0].route)
        assertEquals("Saved", options[1].title)
        assertEquals(Routes.SavedHub, options[1].route)
    }

    // ---------- preferenceOptions ----------

    @Test
    fun `preferenceOptions contains Default Location and Notifications`() {
        val options = viewModel.preferenceOptions.value

        assertEquals(2, options.size)
        assertEquals("Default Location", options[0].title)
        assertEquals(Routes.DefaultLocation, options[0].route)
        assertEquals("Notifications", options[1].title)
        assertEquals(Routes.Notifications, options[1].route)
    }

    // ---------- aboutOptions ----------

    @Test
    fun `aboutOptions contains Privacy Policy and About App`() {
        val options = viewModel.aboutOptions.value

        assertEquals(2, options.size)
        assertEquals("Privacy Policy", options[0].title)
        assertEquals(Routes.PrivacyPolicy, options[0].route)
        assertEquals("About App", options[1].title)
        assertEquals(Routes.AboutApp, options[1].route)
    }

    // ---------- logout ----------

    @Test
    fun `logout calls tokenManager clearTokens`() = runTest {
        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 1) { tokenManager.clearTokens() }
    }

    @Test
    fun `logout can be called multiple times`() = runTest {
        viewModel.logout()
        advanceUntilIdle()
        viewModel.logout()
        advanceUntilIdle()

        coVerify(exactly = 2) { tokenManager.clearTokens() }
    }

    @Test
    fun `logout does not crash when clearTokens throws`() = runTest {
        coEvery { tokenManager.clearTokens() } throws RuntimeException("storage fail")

        viewModel.logout()
        advanceUntilIdle()

        // If we got here, the ViewModel handled the exception.
        coVerify(exactly = 1) { tokenManager.clearTokens() }
    }


    @Test
    fun `all three option groups are non-empty on init`() {
        assertTrue(viewModel.accountOptions.value.isNotEmpty())
        assertTrue(viewModel.preferenceOptions.value.isNotEmpty())
        assertTrue(viewModel.aboutOptions.value.isNotEmpty())
    }
}