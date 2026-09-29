package com.emirgasic.forecastfm.feature.auth.login

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.AuthRepository
import com.emirgasic.forecastfm.data.repository.LoginResult
import com.emirgasic.forecastfm.network.auth.response.AuthResponse
import com.emirgasic.forecastfm.network.auth.response.AuthUser
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    private var loginSuccessCalled = false

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk(relaxed = true)
        authRepository = mockk()
        loginSuccessCalled = false
        viewModel = LoginViewModel(
            tokenManager = tokenManager,
            authRepository = authRepository,
            onLoginSuccess = { loginSuccessCalled = true }
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `updateEmail updates email state`() {
        viewModel.updateEmail("user@test.com")
        assertEquals("user@test.com", viewModel.email.value)
    }

    @Test
    fun `updateEmail clears previous error`() {
        viewModel.updateEmail("x")
        viewModel.updatePassword("y")
        viewModel.login()

    }

    @Test
    fun `updatePassword updates password state`() {
        viewModel.updatePassword("secret123")
        assertEquals("secret123", viewModel.password.value)
    }


    @Test
    fun `login with blank email sets error and does not call repository`() = runTest {
        viewModel.updatePassword("password")
        viewModel.login()

        assertEquals("Email and password are required", viewModel.errorMessage.value)
        assertFalse(loginSuccessCalled)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `login with blank password sets error and does not call repository`() = runTest {
        viewModel.updateEmail("user@test.com")
        viewModel.login()

        assertEquals("Email and password are required", viewModel.errorMessage.value)
        assertFalse(loginSuccessCalled)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `login with both blank sets error`() = runTest {
        viewModel.login()

        assertEquals("Email and password are required", viewModel.errorMessage.value)
        assertFalse(loginSuccessCalled)
    }


    @Test
    fun `login success saves tokens calls callback and clears error`() = runTest {
        val fakeResponse = fakeAuthResponse()
        coEvery { authRepository.login("user@test.com", "password") } returns
                LoginResult.Success(fakeResponse)

        viewModel.updateEmail("user@test.com")
        viewModel.updatePassword("password")
        viewModel.login()

        advanceUntilIdle()

        assertTrue(loginSuccessCalled)
        assertNull(viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)

        coVerify(exactly = 1) {
            tokenManager.saveTokens(
                token = "fake-token",
                refreshToken = "fake-refresh",
                userId = "user-123",
                email = "user@test.com"
            )
        }
    }




    @Test
    fun `login returns Error sets error message and does not call callback`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns
                LoginResult.Error("Invalid email or password")

        viewModel.updateEmail("user@test.com")
        viewModel.updatePassword("wrong")
        viewModel.login()

        advanceUntilIdle()

        assertEquals("Invalid email or password", viewModel.errorMessage.value)
        assertFalse(loginSuccessCalled)
        assertFalse(viewModel.isLoading.value)
        coVerify(exactly = 0) { tokenManager.saveTokens(any(), any(), any(), any()) }
    }

    @Test
    fun `login throws Exception sets generic error message`() = runTest {
        coEvery { authRepository.login(any(), any()) } throws RuntimeException("connect timeout")

        viewModel.updateEmail("user@test.com")
        viewModel.updatePassword("password")
        viewModel.login()

        advanceUntilIdle()

        assertEquals(
            "Cannot connect to server. Please check your connection.",
            viewModel.errorMessage.value
        )
        assertFalse(loginSuccessCalled)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `login throws non connect Exception includes message in error`() = runTest {
        coEvery { authRepository.login(any(), any()) } throws RuntimeException("boom")

        viewModel.updateEmail("user@test.com")
        viewModel.updatePassword("password")
        viewModel.login()

        advanceUntilIdle()

        assertEquals("Login failed: boom", viewModel.errorMessage.value)
    }


    private fun fakeAuthResponse() = AuthResponse(
        token = "fake-token",
        refreshToken = "fake-refresh",
        user = AuthUser(
            id = "user-123",
            email = "user@test.com",
            username = "testuser"
        ),
        expiresAt = 9999999999L
    )
}