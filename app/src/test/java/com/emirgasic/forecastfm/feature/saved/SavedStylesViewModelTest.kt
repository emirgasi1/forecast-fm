package com.emirgasic.forecastfm.feature.saved

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.SavedOutfitRepository
import com.emirgasic.forecastfm.network.outfit.OutfitResponse
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
class SavedStylesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var savedOutfitRepository: SavedOutfitRepository
    private lateinit var viewModel: SavedStylesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        savedOutfitRepository = mockk(relaxed = true)
        every { tokenManager.getUserId() } returns flowOf("user-123")
        viewModel = SavedStylesViewModel(
            tokenManager = tokenManager,
            savedOutfitRepository = savedOutfitRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial savedStyles is empty`() {
        assertTrue(viewModel.savedStyles.value.isEmpty())
    }

    @Test
    fun `initial isLoading is false`() {
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- loadSavedStyles ----------

    @Test
    fun `loadSavedStyles success populates savedStyles`() = runTest {
        coEvery { savedOutfitRepository.getSavedOutfits("user-123") } returns
                listOf(fakeOutfitResponse("o1"), fakeOutfitResponse("o2"))

        viewModel.loadSavedStyles()
        advanceUntilIdle()

        assertEquals(2, viewModel.savedStyles.value.size)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadSavedStyles prefixes relative image urls with base url`() = runTest {
        coEvery { savedOutfitRepository.getSavedOutfits(any()) } returns
                listOf(fakeOutfitResponse("o1", imageUrl = "/img.jpg"))

        viewModel.loadSavedStyles()
        advanceUntilIdle()

        val imageUrl = viewModel.savedStyles.value.first().imageUrl
        assertTrue("Expected image URL to contain base URL, got: $imageUrl",
            imageUrl.startsWith("http"))
        assertTrue(imageUrl.contains("/img.jpg"))
    }

    @Test
    fun `loadSavedStyles leaves absolute image urls unchanged`() = runTest {
        coEvery { savedOutfitRepository.getSavedOutfits(any()) } returns
                listOf(fakeOutfitResponse("o1", imageUrl = "https://cdn.example.com/x.jpg"))

        viewModel.loadSavedStyles()
        advanceUntilIdle()

        assertEquals(
            "https://cdn.example.com/x.jpg",
            viewModel.savedStyles.value.first().imageUrl
        )
    }

    @Test
    fun `loadSavedStyles with no user does not call repository`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadSavedStyles()
        advanceUntilIdle()

        coVerify(exactly = 0) { savedOutfitRepository.getSavedOutfits(any()) }
    }

    @Test
    fun `loadSavedStyles with repository exception leaves list empty`() = runTest {
        coEvery { savedOutfitRepository.getSavedOutfits(any()) } throws
                RuntimeException("api down")

        viewModel.loadSavedStyles()
        advanceUntilIdle()

        assertTrue(viewModel.savedStyles.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    // ---------- unsaveStyle ----------

    @Test
    fun `unsaveStyle removes from list`() = runTest {
        coEvery { savedOutfitRepository.getSavedOutfits(any()) } returns
                listOf(fakeOutfitResponse("o1"), fakeOutfitResponse("o2"))
        viewModel.loadSavedStyles()
        advanceUntilIdle()

        viewModel.unsaveStyle("o1")
        advanceUntilIdle()

        assertEquals(1, viewModel.savedStyles.value.size)
        assertEquals("o2", viewModel.savedStyles.value.first().id)
        coVerify(exactly = 1) { savedOutfitRepository.unsaveOutfit("o1", "user-123") }
    }

    @Test
    fun `unsaveStyle with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.unsaveStyle("o1")
        advanceUntilIdle()

        coVerify(exactly = 0) { savedOutfitRepository.unsaveOutfit(any(), any()) }
    }

    // ---------- helpers ----------

    private fun fakeOutfitResponse(
        id: String,
        imageUrl: String = "/default.jpg"
    ) = OutfitResponse(
        id = id,
        userId = "user-123",
        imageUrl = imageUrl,
        title = "Title $id",
        weatherCondition = "Sunny",
        season = "Summer",
        likes = 0,
        storeName = null,
        storeAddress = null,
        price = null,
        storePhone = null,
        productUrl = null,
        createdAt = ""
    )
}