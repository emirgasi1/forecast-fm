package com.emirgasic.forecastfm.feature.settings.edit_profile

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.ProfileRepository
import com.emirgasic.forecastfm.data.repository.UserRepository
import com.emirgasic.forecastfm.network.location.LocationApi
import com.emirgasic.forecastfm.network.location.LocationResponse
import com.emirgasic.forecastfm.network.user.UserResponse
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
class EditProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var profileRepository: ProfileRepository
    private lateinit var userRepository: UserRepository
    private lateinit var locationApi: LocationApi
    private lateinit var viewModel: EditProfileViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        profileRepository = mockk(relaxed = true)
        userRepository = mockk()
        locationApi = mockk(relaxed = true)

        every { tokenManager.getUserId() } returns flowOf("user-123")

        viewModel = EditProfileViewModel(
            tokenManager = tokenManager,
            profileRepository = profileRepository,
            userRepository = userRepository,
            locationApi = locationApi
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial state isLoading true`() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial username is empty`() {
        assertEquals("", viewModel.uiState.value.username)
    }

    // ---------- loadProfile ----------

    @Test
    fun `loadProfile populates all fields from user response`() = runTest {
        coEvery { userRepository.getUser("user-123") } returns UserResponse(
            id = "user-123",
            username = "emir",
            bio = "Hello",
            profileImageUrl = "/pic.jpg",
            favoriteLocation = "Baščaršija"
        )

        viewModel.loadProfile()
        advanceUntilIdle()

        val s = viewModel.uiState.value
        assertEquals("emir", s.username)
        assertEquals("Hello", s.bio)
        assertEquals("/pic.jpg", s.profileImageUrl)
        assertEquals("Baščaršija", s.favoriteLocation)
        assertFalse(s.isLoading)
        assertNull(s.error)
    }

    @Test
    fun `loadProfile with no user sets error`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.loadProfile()
        advanceUntilIdle()

        assertEquals("User not logged in", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadProfile with null user response sets error`() = runTest {
        coEvery { userRepository.getUser(any()) } returns null

        viewModel.loadProfile()
        advanceUntilIdle()

        assertEquals("User not found", viewModel.uiState.value.error)
    }

    @Test
    fun `loadProfile with repository exception sets error`() = runTest {
        coEvery { userRepository.getUser(any()) } throws RuntimeException("api down")

        viewModel.loadProfile()
        advanceUntilIdle()

        assertEquals("api down", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loadProfile maps null bio and favoriteLocation to empty strings`() = runTest {
        coEvery { userRepository.getUser(any()) } returns UserResponse(
            id = "user-123",
            username = "emir",
            bio = null,
            profileImageUrl = null,
            favoriteLocation = null
        )

        viewModel.loadProfile()
        advanceUntilIdle()

        val s = viewModel.uiState.value
        assertEquals("", s.bio)
        assertEquals("", s.profileImageUrl)
        assertEquals("", s.favoriteLocation)
    }

    // ---------- loadLocations ----------

    @Test
    fun `loadLocations populates locations list`() = runTest {
        coEvery { locationApi.getLocations() } returns listOf(
            fakeLocation("loc-1", "Baščaršija"),
            fakeLocation("loc-2", "Marijin Dvor")
        )

        viewModel.loadLocations()
        advanceUntilIdle()

        assertEquals(listOf("Baščaršija", "Marijin Dvor"), viewModel.uiState.value.locations)
    }

    @Test
    fun `loadLocations with exception leaves locations empty`() = runTest {
        coEvery { locationApi.getLocations() } throws RuntimeException("api down")

        viewModel.loadLocations()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.locations.isEmpty())
    }

    // ---------- setters ----------

    @Test
    fun `updateUsername updates state`() {
        viewModel.updateUsername("newname")
        assertEquals("newname", viewModel.uiState.value.username)
    }

    @Test
    fun `updateBio updates state`() {
        viewModel.updateBio("new bio")
        assertEquals("new bio", viewModel.uiState.value.bio)
    }

    @Test
    fun `updateLocation updates state`() {
        viewModel.updateLocation("Baščaršija")
        assertEquals("Baščaršija", viewModel.uiState.value.favoriteLocation)
    }

    // ---------- uploadImage ----------

    @Test
    fun `uploadImage success sets profileImageUrl and savedMessage`() = runTest {
        // Load profile first to populate currentUserId
        coEvery { userRepository.getUser(any()) } returns UserResponse(
            id = "user-123",
            username = "emir",
            bio = null,
            profileImageUrl = null,
            favoriteLocation = null
        )
        viewModel.loadProfile()
        advanceUntilIdle()

        var capturedUri: String? = null
        viewModel.uploadImage(
            imageUri = "content://image/1",
            uploadImage = { userId, imageUri ->
                capturedUri = imageUri
                "https://cdn.com/new.jpg"
            }
        )
        advanceUntilIdle()

        assertEquals("https://cdn.com/new.jpg", viewModel.uiState.value.profileImageUrl)
        assertEquals("Image uploaded", viewModel.uiState.value.savedMessage)
        assertEquals("content://image/1", capturedUri)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun `uploadImage with null result sets error`() = runTest {
        coEvery { userRepository.getUser(any()) } returns UserResponse(
            id = "user-123", username = "emir", bio = null,
            profileImageUrl = null, favoriteLocation = null
        )
        viewModel.loadProfile()
        advanceUntilIdle()

        viewModel.uploadImage(
            imageUri = "content://image/1",
            uploadImage = { _, _ -> null }
        )
        advanceUntilIdle()

        assertEquals("Image upload failed", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun `uploadImage with exception sets error`() = runTest {
        coEvery { userRepository.getUser(any()) } returns UserResponse(
            id = "user-123", username = "emir", bio = null,
            profileImageUrl = null, favoriteLocation = null
        )
        viewModel.loadProfile()
        advanceUntilIdle()

        viewModel.uploadImage(
            imageUri = "content://image/1",
            uploadImage = { _, _ -> throw RuntimeException("network fail") }
        )
        advanceUntilIdle()

        assertEquals("network fail", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    // ---------- saveProfile ----------

    @Test
    fun `saveProfile success sets savedMessage`() = runTest {
        coEvery { userRepository.getUser(any()) } returns UserResponse(
            id = "user-123", username = "emir", bio = null,
            profileImageUrl = null, favoriteLocation = null
        )
        viewModel.loadProfile()
        advanceUntilIdle()

        coEvery { profileRepository.updateProfile(any(), any(), any(), any()) } returns true

        viewModel.updateUsername("newname")
        viewModel.updateBio("new bio")
        viewModel.saveProfile()
        advanceUntilIdle()

        assertEquals("Profile saved", viewModel.uiState.value.savedMessage)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isSaving)
        coVerify(exactly = 1) {
            profileRepository.updateProfile("user-123", "newname", "new bio", any())
        }
    }

    @Test
    fun `saveProfile when updateProfile returns false sets error`() = runTest {
        coEvery { userRepository.getUser(any()) } returns UserResponse(
            id = "user-123", username = "emir", bio = null,
            profileImageUrl = null, favoriteLocation = null
        )
        viewModel.loadProfile()
        advanceUntilIdle()

        coEvery { profileRepository.updateProfile(any(), any(), any(), any()) } returns false

        viewModel.saveProfile()
        advanceUntilIdle()

        assertEquals("Failed to save profile", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.savedMessage)
    }

    @Test
    fun `saveProfile before loadProfile does nothing`() = runTest {
        // currentUserId is null because loadProfile wasn't called
        viewModel.saveProfile()
        advanceUntilIdle()

        coVerify(exactly = 0) { profileRepository.updateProfile(any(), any(), any(), any()) }
    }

    // ---------- clearMessages ----------

    @Test
    fun `clearMessages resets savedMessage and error`() = runTest {
        coEvery { userRepository.getUser(any()) } returns UserResponse(
            id = "user-123", username = "emir", bio = null,
            profileImageUrl = null, favoriteLocation = null
        )
        viewModel.loadProfile()
        advanceUntilIdle()
        coEvery { profileRepository.updateProfile(any(), any(), any(), any()) } returns true
        viewModel.saveProfile()
        advanceUntilIdle()

        assertEquals("Profile saved", viewModel.uiState.value.savedMessage)

        viewModel.clearMessages()

        assertNull(viewModel.uiState.value.savedMessage)
        assertNull(viewModel.uiState.value.error)
    }

    // ---------- helpers ----------

    private fun fakeLocation(id: String, name: String) = LocationResponse(
        id = id,
        name = name,
        description = "",
        latitude = 43.85,
        longitude = 18.41
    )
}