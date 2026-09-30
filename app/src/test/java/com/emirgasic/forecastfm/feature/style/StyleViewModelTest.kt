package com.emirgasic.forecastfm.feature.style

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.Outfit
import com.emirgasic.forecastfm.data.repository.OutfitRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StyleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var outfitRepository: OutfitRepository
    private lateinit var savedOutfitRepository: SavedOutfitRepository
    private lateinit var viewModel: StyleViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        outfitRepository = mockk(relaxed = true)
        savedOutfitRepository = mockk(relaxed = true)
        every { tokenManager.getUserId() } returns flowOf("user-123")
        viewModel = StyleViewModel(
            tokenManager = tokenManager,
            outfitRepository = outfitRepository,
            savedOutfitRepository = savedOutfitRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial style is null`() {
        assertNull(viewModel.style.value)
    }

    @Test
    fun `initial isLoading is false`() {
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `initial savedOutfitIds is empty`() {
        assertTrue(viewModel.savedOutfitIds.value.isEmpty())
    }

    // ---------- loadStyle ----------

    @Test
    fun `loadStyle success populates style with outfits`() = runTest {
        coEvery { outfitRepository.getTrendingOutfits() } returns
                listOf(fakeOutfit("o1"), fakeOutfit("o2"))

        viewModel.loadStyle()
        advanceUntilIdle()

        assertEquals(2, viewModel.style.value?.outfits?.size)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadStyle with repository exception sets error`() = runTest {
        coEvery { outfitRepository.getTrendingOutfits() } throws
                RuntimeException("api down")

        viewModel.loadStyle()
        advanceUntilIdle()

        assertEquals("api down", viewModel.error.value)
        assertNull(viewModel.style.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `loadStyle clears previous error on retry`() = runTest {
        coEvery { outfitRepository.getTrendingOutfits() } throws
                RuntimeException("first fail")

        viewModel.loadStyle()
        advanceUntilIdle()
        assertEquals("first fail", viewModel.error.value)

        coEvery { outfitRepository.getTrendingOutfits() } returns listOf(fakeOutfit("o1"))
        viewModel.loadStyle()
        advanceUntilIdle()

        assertNull(viewModel.error.value)
        assertEquals(1, viewModel.style.value?.outfits?.size)
    }

    // ---------- loadSavedOutfits ----------

    @Test
    fun `loadSavedOutfits populates savedOutfitIds`() = runTest {
        coEvery { savedOutfitRepository.getSavedOutfits("user-123") } returns
                listOf(fakeOutfitResponse("o1"), fakeOutfitResponse("o3"))

        viewModel.loadSavedOutfits()
        advanceUntilIdle()

        assertEquals(setOf("o1", "o3"), viewModel.savedOutfitIds.value)
    }

    @Test
    fun `loadSavedOutfits with no user does not call repository`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadSavedOutfits()
        advanceUntilIdle()

        coVerify(exactly = 0) { savedOutfitRepository.getSavedOutfits(any()) }
    }

    @Test
    fun `loadSavedOutfits with repository exception leaves ids empty`() = runTest {
        coEvery { savedOutfitRepository.getSavedOutfits(any()) } throws
                RuntimeException("api down")

        viewModel.loadSavedOutfits()
        advanceUntilIdle()

        assertTrue(viewModel.savedOutfitIds.value.isEmpty())
    }

    // ---------- toggleSaveOutfit ----------

    @Test
    fun `toggleSaveOutfit on unsaved outfit calls saveOutfit and adds id`() = runTest {
        viewModel.toggleSaveOutfit("o1")
        advanceUntilIdle()

        coVerify(exactly = 1) { savedOutfitRepository.saveOutfit("o1", "user-123") }
        assertTrue("o1" in viewModel.savedOutfitIds.value)
    }

    @Test
    fun `toggleSaveOutfit on saved outfit calls unsaveOutfit and removes id`() = runTest {
        coEvery { savedOutfitRepository.getSavedOutfits(any()) } returns
                listOf(fakeOutfitResponse("o1"))
        viewModel.loadSavedOutfits()
        advanceUntilIdle()
        assertTrue("o1" in viewModel.savedOutfitIds.value)

        viewModel.toggleSaveOutfit("o1")
        advanceUntilIdle()

        coVerify(exactly = 1) { savedOutfitRepository.unsaveOutfit("o1", "user-123") }
        assertFalse("o1" in viewModel.savedOutfitIds.value)
    }

    @Test
    fun `toggleSaveOutfit with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.toggleSaveOutfit("o1")
        advanceUntilIdle()

        coVerify(exactly = 0) { savedOutfitRepository.saveOutfit(any(), any()) }
        coVerify(exactly = 0) { savedOutfitRepository.unsaveOutfit(any(), any()) }
    }

    @Test
    fun `toggleSaveOutfit with repository exception leaves state unchanged`() = runTest {
        coEvery { savedOutfitRepository.saveOutfit(any(), any()) } throws
                RuntimeException("api down")

        viewModel.toggleSaveOutfit("o1")
        advanceUntilIdle()

        assertFalse("o1" in viewModel.savedOutfitIds.value)
    }

    // ---------- likeOutfit ----------

    @Test
    fun `likeOutfit calls repository and reloads style`() = runTest {
        coEvery { outfitRepository.getTrendingOutfits() } returns listOf(fakeOutfit("o1"))

        viewModel.likeOutfit("o1")
        advanceUntilIdle()

        coVerify(exactly = 1) { outfitRepository.likeOutfit("o1") }
        coVerify(exactly = 1) { outfitRepository.getTrendingOutfits() }
    }

    @Test
    fun `likeOutfit with repository exception does not crash`() = runTest {
        coEvery { outfitRepository.likeOutfit(any()) } throws RuntimeException("api down")

        viewModel.likeOutfit("o1")
        advanceUntilIdle()

        // No exception thrown, no style loaded (loadStyle wasn't called because likeOutfit failed first)
        assertNull(viewModel.style.value)
    }

    // ---------- helpers ----------

    private fun fakeOutfit(id: String) = Outfit(
        id = id,
        userId = "user-123",
        imageUrl = "https://example.com/$id.jpg",
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

    private fun fakeOutfitResponse(id: String) = OutfitResponse(
        id = id,
        userId = "user-123",
        imageUrl = "/$id.jpg",
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