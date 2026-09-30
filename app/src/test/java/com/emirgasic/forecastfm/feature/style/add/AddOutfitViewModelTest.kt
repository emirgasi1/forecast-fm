package com.emirgasic.forecastfm.feature.style.add

import android.net.Uri
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.network.outfit.OutfitApi
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
class AddOutfitViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var outfitApi: OutfitApi
    private lateinit var viewModel: AddOutfitViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        outfitApi = mockk(relaxed = true)
        every { tokenManager.getUserId() } returns flowOf("user-123")
        viewModel = AddOutfitViewModel(
            tokenManager = tokenManager,
            outfitApi = outfitApi
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty fields`() {
        val s = viewModel.state.value
        assertNull(s.imageUri)
        assertEquals("", s.title)
        assertEquals("", s.weatherCondition)
        assertEquals("", s.season)
        assertFalse(s.isUploading)
        assertNull(s.error)
    }

    @Test
    fun `initial canSubmit is false`() {
        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun `setTitle updates title`() {
        viewModel.setTitle("Cool Outfit")
        assertEquals("Cool Outfit", viewModel.state.value.title)
    }

    @Test
    fun `setWeatherCondition updates weather`() {
        viewModel.setWeatherCondition("Sunny")
        assertEquals("Sunny", viewModel.state.value.weatherCondition)
    }

    @Test
    fun `setSeason updates season`() {
        viewModel.setSeason("Summer")
        assertEquals("Summer", viewModel.state.value.season)
    }

    @Test
    fun `setImage updates image uri`() {
        val uri = mockk<Uri>()
        viewModel.setImage(uri)
        assertEquals(uri, viewModel.state.value.imageUri)
    }

    @Test
    fun `canSubmit is true when all required fields filled`() {
        viewModel.setImage(mockk())
        viewModel.setTitle("Title")
        viewModel.setWeatherCondition("Sunny")
        viewModel.setSeason("Summer")

        assertTrue(viewModel.state.value.canSubmit)
    }

    @Test
    fun `canSubmit is false when image missing`() {
        viewModel.setTitle("Title")
        viewModel.setWeatherCondition("Sunny")
        viewModel.setSeason("Summer")

        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun `canSubmit is false when title blank`() {
        viewModel.setImage(mockk())
        viewModel.setTitle("   ")
        viewModel.setWeatherCondition("Sunny")
        viewModel.setSeason("Summer")

        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun `submit with no image does nothing`() = runTest {
        var called = false
        viewModel.submit(uploadImage = { "url" }, onSuccess = { called = true })
        advanceUntilIdle()

        assertFalse(called)
        coVerify(exactly = 0) {
            outfitApi.createOutfit(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `submit with no user sets error`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)
        fillRequiredFields()

        viewModel.submit(uploadImage = { "url" }, onSuccess = {})
        advanceUntilIdle()

        assertEquals("User not logged in", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isUploading)
    }

    @Test
    fun `submit success calls createOutfit and onSuccess`() = runTest {
        fillRequiredFields()
        var successCalled = false

        viewModel.submit(
            uploadImage = { "https://cdn.com/img.jpg" },
            onSuccess = { successCalled = true }
        )
        advanceUntilIdle()

        assertTrue(successCalled)
        assertFalse(viewModel.state.value.isUploading)
        assertNull(viewModel.state.value.error)

        coVerify(exactly = 1) {
            outfitApi.createOutfit(
                userId = "user-123",
                imageUrl = "https://cdn.com/img.jpg",
                title = "Cool Outfit",
                weatherCondition = "Sunny",
                season = "Summer",
                storeName = null,
                storeAddress = null,
                price = null,
                storePhone = null,
                productUrl = null
            )
        }
    }

    @Test
    fun `submit passes optional fields when non-blank`() = runTest {
        fillRequiredFields()
        viewModel.setStoreName("Zara")
        viewModel.setStoreAddress("SCC")
        viewModel.setPrice("60-100 KM")

        viewModel.submit(uploadImage = { "url" }, onSuccess = {})
        advanceUntilIdle()

        coVerify(exactly = 1) {
            outfitApi.createOutfit(
                userId = any(),
                imageUrl = any(),
                title = any(),
                weatherCondition = any(),
                season = any(),
                storeName = "Zara",
                storeAddress = "SCC",
                price = "60-100 KM",
                storePhone = null,
                productUrl = null
            )
        }
    }

    @Test
    fun `submit when uploadImage throws sets error`() = runTest {
        fillRequiredFields()

        viewModel.submit(
            uploadImage = { throw RuntimeException("upload failed") },
            onSuccess = {}
        )
        advanceUntilIdle()

        assertEquals("upload failed", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isUploading)
    }

    @Test
    fun `submit when createOutfit throws sets error`() = runTest {
        fillRequiredFields()
        coEvery {
            outfitApi.createOutfit(any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        } throws RuntimeException("create failed")

        var called = false
        viewModel.submit(
            uploadImage = { "url" },
            onSuccess = { called = true }
        )
        advanceUntilIdle()

        assertEquals("create failed", viewModel.state.value.error)
        assertFalse(called)
    }

    @Test
    fun `submit trims title before creating outfit`() = runTest {
        fillRequiredFields()
        viewModel.setTitle("  Cool Outfit  ")

        viewModel.submit(uploadImage = { "url" }, onSuccess = {})
        advanceUntilIdle()

        coVerify(exactly = 1) {
            outfitApi.createOutfit(
                userId = any(),
                imageUrl = any(),
                title = "Cool Outfit",
                weatherCondition = any(),
                season = any(),
                storeName = any(),
                storeAddress = any(),
                price = any(),
                storePhone = any(),
                productUrl = any()
            )
        }
    }

    private fun fillRequiredFields() {
        viewModel.setImage(mockk())
        viewModel.setTitle("Cool Outfit")
        viewModel.setWeatherCondition("Sunny")
        viewModel.setSeason("Summer")
    }
}