package com.emirgasic.forecastfm.feature.auth.forgotpassword

import com.emirgasic.forecastfm.data.repository.AuthRepository
import com.emirgasic.forecastfm.data.repository.ForgotPasswordResult
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
class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: ForgotPasswordViewModel

    private var successCalled = false

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        successCalled = false
        viewModel = ForgotPasswordViewModel(
            authRepository = authRepository,
            onSuccess = { successCalled = true }
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
    fun `updateEmail clears error and success messages`() = runTest {
        // trigger a validation error
        viewModel.sendResetLink()
        assertEquals("Email is required", viewModel.errorMessage.value)

        viewModel.updateEmail("now@test.com")
        assertNull(viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)
    }


    @Test
    fun `sendResetLink with blank email sets error and does not call repository`() = runTest {
        viewModel.sendResetLink()

        assertEquals("Email is required", viewModel.errorMessage.value)
        assertFalse(successCalled)
        coVerify(exactly = 0) { authRepository.forgotPassword(any()) }
    }

    @Test
    fun `sendResetLink with invalid email format sets error`() = runTest {
        viewModel.updateEmail("not-an-email")
        viewModel.sendResetLink()

        assertEquals("Invalid email format", viewModel.errorMessage.value)
        assertFalse(successCalled)
        coVerify(exactly = 0) { authRepository.forgotPassword(any()) }
    }


    @Test
    fun `sendResetLink success sets success message and calls onSuccess after delay`() = runTest {
        coEvery { authRepository.forgotPassword(any()) } returns ForgotPasswordResult.Success

        viewModel.updateEmail("user@test.com")
        viewModel.sendResetLink()


        advanceUntilIdle()

        assertEquals("Password reset link sent to your email", viewModel.successMessage.value)
        assertNull(viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
        assertTrue(successCalled)

        coVerify(exactly = 1) { authRepository.forgotPassword("user@test.com") }
    }

    @Test
    fun `sendResetLink success sets success message before callback fires`() = runTest {
        coEvery { authRepository.forgotPassword(any()) } returns ForgotPasswordResult.Success

        viewModel.updateEmail("user@test.com")
        viewModel.sendResetLink()

        testDispatcher.scheduler.advanceTimeBy(1000)

        assertEquals("Password reset link sent to your email", viewModel.successMessage.value)
        assertFalse("onSuccess should not fire before the delay elapses", successCalled)
    }


    @Test
    fun `sendResetLink returns Error sets error message and does not call onSuccess`() = runTest {
        coEvery { authRepository.forgotPassword(any()) } returns
                ForgotPasswordResult.Error("Email not found")

        viewModel.updateEmail("missing@test.com")
        viewModel.sendResetLink()

        advanceUntilIdle()

        assertEquals("Email not found", viewModel.errorMessage.value)
        assertNull(viewModel.successMessage.value)
        assertFalse(successCalled)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `sendResetLink throws connect Exception sets connection error`() = runTest {
        coEvery { authRepository.forgotPassword(any()) } throws RuntimeException("connect timeout")

        viewModel.updateEmail("user@test.com")
        viewModel.sendResetLink()

        advanceUntilIdle()

        assertEquals(
            "Cannot connect to server. Please check your connection.",
            viewModel.errorMessage.value
        )
        assertFalse(successCalled)
    }

    @Test
    fun `sendResetLink throws non connect Exception includes message`() = runTest {
        coEvery { authRepository.forgotPassword(any()) } throws RuntimeException("boom")

        viewModel.updateEmail("user@test.com")
        viewModel.sendResetLink()

        advanceUntilIdle()

        assertEquals("Failed to send reset link: boom", viewModel.errorMessage.value)
    }
}