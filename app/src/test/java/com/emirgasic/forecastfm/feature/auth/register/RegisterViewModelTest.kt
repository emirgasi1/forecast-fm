package com.emirgasic.forecastfm.feature.auth.register

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.AuthRepository
import com.emirgasic.forecastfm.data.repository.RegisterResult
import com.emirgasic.forecastfm.network.auth.response.AuthUser
import com.emirgasic.forecastfm.network.auth.response.RegisterResponse
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
class RegisterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: RegisterViewModel

    private var registerSuccessCalled = false

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk(relaxed = true)
        authRepository = mockk()
        registerSuccessCalled = false
        viewModel = RegisterViewModel(
            tokenManager = tokenManager,
            authRepository = authRepository,
            onRegisterSuccess = { registerSuccessCalled = true }
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
    fun `updateUsername updates username state`() {
        viewModel.updateUsername("emir")
        assertEquals("emir", viewModel.username.value)
    }

    @Test
    fun `updatePassword updates password state`() {
        viewModel.updatePassword("password123")
        assertEquals("password123", viewModel.password.value)
    }

    @Test
    fun `updateConfirmPassword updates confirmPassword state`() {
        viewModel.updateConfirmPassword("password123")
        assertEquals("password123", viewModel.confirmPassword.value)
    }

    @Test
    fun `updateCheckMark updates checkMark state`() {
        viewModel.updateCheckMark(true)
        assertTrue(viewModel.checkMark.value)
    }

    @Test
    fun `updateEmail clears previous error`() = runTest {
        viewModel.register()  // all blank -> sets error
        assertEquals("Email is required", viewModel.errorMessage.value)

        viewModel.updateEmail("now@test.com")
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `updateUsername clears previous error`() = runTest {
        viewModel.register()
        viewModel.updateUsername("emir")
        assertNull(viewModel.errorMessage.value)
    }


    @Test
    fun `register with blank email sets error`() = runTest {
        viewModel.register()
        assertEquals("Email is required", viewModel.errorMessage.value)
        coVerify(exactly = 0) { authRepository.register(any(), any(), any(), any()) }
    }

    @Test
    fun `register with invalid email format sets error`() = runTest {
        viewModel.updateEmail("not-an-email")
        viewModel.register()
        assertEquals("Invalid email format", viewModel.errorMessage.value)
    }

    @Test
    fun `register with blank username sets error`() = runTest {
        viewModel.updateEmail("user@test.com")
        viewModel.register()
        assertEquals("Username is required", viewModel.errorMessage.value)
    }

    @Test
    fun `register with short username sets error`() = runTest {
        viewModel.updateEmail("user@test.com")
        viewModel.updateUsername("ab")
        viewModel.register()
        assertEquals("Username must be at least 3 characters", viewModel.errorMessage.value)
    }

    @Test
    fun `register with blank password sets error`() = runTest {
        viewModel.updateEmail("user@test.com")
        viewModel.updateUsername("emir")
        viewModel.register()
        assertEquals("Password is required", viewModel.errorMessage.value)
    }

    @Test
    fun `register with short password sets error`() = runTest {
        viewModel.updateEmail("user@test.com")
        viewModel.updateUsername("emir")
        viewModel.updatePassword("short")
        viewModel.register()
        assertEquals("Password must be at least 8 characters", viewModel.errorMessage.value)
    }

    @Test
    fun `register with mismatched passwords sets error`() = runTest {
        viewModel.updateEmail("user@test.com")
        viewModel.updateUsername("emir")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("different")
        viewModel.register()
        assertEquals("Passwords do not match", viewModel.errorMessage.value)
    }

    @Test
    fun `register without accepting terms sets error`() = runTest {
        viewModel.updateEmail("user@test.com")
        viewModel.updateUsername("emir")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")
        viewModel.updateCheckMark(false)
        viewModel.register()
        assertEquals("You must agree to the Terms & Privacy", viewModel.errorMessage.value)
    }


    @Test
    fun `register success saves tokens calls callback and clears error`() = runTest {
        coEvery {
            authRepository.register(any(), any(), any(), any())
        } returns RegisterResult.Success(fakeRegisterResponse())

        fillValidForm()
        viewModel.register()

        advanceUntilIdle()

        assertTrue(registerSuccessCalled)
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
    fun `register returns Error sets error message and does not call callback`() = runTest {
        coEvery {
            authRepository.register(any(), any(), any(), any())
        } returns RegisterResult.Error("Email or username already taken")

        fillValidForm()
        viewModel.register()

        advanceUntilIdle()

        assertEquals("Email or username already taken", viewModel.errorMessage.value)
        assertFalse(registerSuccessCalled)
        assertFalse(viewModel.isLoading.value)
        coVerify(exactly = 0) { tokenManager.saveTokens(any(), any(), any(), any()) }
    }

    @Test
    fun `register throws connect Exception sets connection error`() = runTest {
        coEvery {
            authRepository.register(any(), any(), any(), any())
        } throws RuntimeException("connect timeout")

        fillValidForm()
        viewModel.register()

        advanceUntilIdle()

        assertEquals(
            "Cannot connect to server. Please check your connection.",
            viewModel.errorMessage.value
        )
        assertFalse(registerSuccessCalled)
    }

    @Test
    fun `register throws non connect Exception includes message`() = runTest {
        coEvery {
            authRepository.register(any(), any(), any(), any())
        } throws RuntimeException("boom")

        fillValidForm()
        viewModel.register()

        advanceUntilIdle()

        assertEquals("Registration failed: boom", viewModel.errorMessage.value)
    }


    private fun fillValidForm() {
        viewModel.updateEmail("user@test.com")
        viewModel.updateUsername("emir")
        viewModel.updatePassword("password123")
        viewModel.updateConfirmPassword("password123")
        viewModel.updateCheckMark(true)
    }

    private fun fakeRegisterResponse() = RegisterResponse(
        token = "fake-token",
        refreshToken = "fake-refresh",
        user = AuthUser(
            id = "user-123",
            email = "user@test.com",
            username = "emir"
        ),
        expiresAt = 9999999999L
    )
}