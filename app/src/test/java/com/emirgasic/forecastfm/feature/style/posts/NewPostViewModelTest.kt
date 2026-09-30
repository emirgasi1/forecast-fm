package com.emirgasic.forecastfm.feature.style.posts

import android.net.Uri
import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.model.NewPost
import com.emirgasic.forecastfm.data.repository.NewPostRepository
import com.emirgasic.forecastfm.network.image.ImageUploadApi
import com.emirgasic.forecastfm.network.post.PostApi
import com.emirgasic.forecastfm.network.post.PostResponse
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
class NewPostViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var newPostRepository: NewPostRepository
    private lateinit var postApi: PostApi
    private lateinit var imageUploadApi: ImageUploadApi
    private lateinit var viewModel: NewPostViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        newPostRepository = mockk()
        postApi = mockk(relaxed = true)
        imageUploadApi = mockk(relaxed = true)

        every { tokenManager.getUserId() } returns flowOf("user-123")
        every { newPostRepository.getNewPostData() } returns NewPost(
            image = null,
            caption = "",
            weather = "",
            location = "",
            selectedPlaylist = "",
            playlists = emptyList()
        )

        viewModel = NewPostViewModel(
            tokenManager = tokenManager,
            newPostRepository = newPostRepository,
            postApi = postApi,
            imageUploadApi = imageUploadApi
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial newPost is null`() {
        assertNull(viewModel.newPost.value)
    }

    @Test
    fun `loadNewPost populates newPost from repository`() {
        viewModel.loadNewPost()
        assertEquals("", viewModel.newPost.value?.caption)
    }

    // ---------- setters ----------

    @Test
    fun `updateCaption updates caption`() {
        viewModel.loadNewPost()
        viewModel.updateCaption("Hello world")
        assertEquals("Hello world", viewModel.newPost.value?.caption)
    }

    @Test
    fun `updateWeather updates weather`() {
        viewModel.loadNewPost()
        viewModel.updateWeather("Sunny")
        assertEquals("Sunny", viewModel.newPost.value?.weather)
    }

    @Test
    fun `updateLocation updates location`() {
        viewModel.loadNewPost()
        viewModel.updateLocation("Baščaršija")
        assertEquals("Baščaršija", viewModel.newPost.value?.location)
    }

    @Test
    fun `updateImage updates image`() {
        viewModel.loadNewPost()
        viewModel.updateImage("content://image/1")
        assertEquals("content://image/1", viewModel.newPost.value?.image)
    }

    @Test
    fun `selectPlaylist updates selectedPlaylist`() {
        viewModel.loadNewPost()
        viewModel.selectPlaylist("Chill Mix")
        assertEquals("Chill Mix", viewModel.newPost.value?.selectedPlaylist)
    }

    // ---------- createPost validation ----------

    @Test
    fun `createPost with no post does nothing`() = runTest {
        viewModel.createPost(uploadImage = { _, _ -> }, onSuccess = {})
        advanceUntilIdle()

        coVerify(exactly = 0) { postApi.createPost(any(), any(), any()) }
    }

    @Test
    fun `createPost with blank caption sets error`() = runTest {
        viewModel.loadNewPost()
        viewModel.updateLocation("Baščaršija")

        viewModel.createPost(uploadImage = { _, _ -> }, onSuccess = {})
        advanceUntilIdle()

        assertEquals("Please add a caption", viewModel.errorMessage.value)
        coVerify(exactly = 0) { postApi.createPost(any(), any(), any()) }
    }

    @Test
    fun `createPost with blank location sets error`() = runTest {
        viewModel.loadNewPost()
        viewModel.updateCaption("Hello world")

        viewModel.createPost(uploadImage = { _, _ -> }, onSuccess = {})
        advanceUntilIdle()

        assertEquals("Please add a location", viewModel.errorMessage.value)
        coVerify(exactly = 0) { postApi.createPost(any(), any(), any()) }
    }

    // ---------- createPost success ----------

    @Test
    fun `createPost success calls postApi and onSuccess`() = runTest {
        viewModel.loadNewPost()
        viewModel.updateCaption("Hello")
        viewModel.updateLocation("Baščaršija")
        coEvery { postApi.createPost(any(), any(), any()) } returns
                fakePostResponse("post-1")

        var successCalled = false
        viewModel.createPost(
            uploadImage = { _, _ -> },
            onSuccess = { successCalled = true }
        )
        advanceUntilIdle()

        assertTrue(successCalled)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)

        coVerify(exactly = 1) {
            postApi.createPost("user-123", "Hello", null)
        }
    }

    @Test
    fun `createPost with image calls uploadImage after post creation`() = runTest {
        viewModel.loadNewPost()
        viewModel.updateCaption("Hello")
        viewModel.updateLocation("Baščaršija")
        viewModel.updateImage("content://image/1")
        coEvery { postApi.createPost(any(), any(), any()) } returns
                fakePostResponse("post-1")

        var uploadedPostId: String? = null
        var uploadedUri: Uri? = null

        viewModel.createPost(
            uploadImage = { postId, uri ->
                uploadedPostId = postId
                uploadedUri = uri
            },
            onSuccess = {}
        )
        advanceUntilIdle()

        assertEquals("post-1", uploadedPostId)
        assertEquals("content://image/1", uploadedUri.toString())
    }

    @Test
    fun `createPost without image does not call uploadImage`() = runTest {
        viewModel.loadNewPost()
        viewModel.updateCaption("Hello")
        viewModel.updateLocation("Baščaršija")
        coEvery { postApi.createPost(any(), any(), any()) } returns
                fakePostResponse("post-1")

        var uploadCalled = false
        viewModel.createPost(
            uploadImage = { _, _ -> uploadCalled = true },
            onSuccess = {}
        )
        advanceUntilIdle()

        assertFalse(uploadCalled)
    }

    @Test
    fun `createPost succeeds even if image upload fails`() = runTest {
        viewModel.loadNewPost()
        viewModel.updateCaption("Hello")
        viewModel.updateLocation("Baščaršija")
        viewModel.updateImage("content://image/1")
        coEvery { postApi.createPost(any(), any(), any()) } returns
                fakePostResponse("post-1")

        var successCalled = false
        viewModel.createPost(
            uploadImage = { _, _ -> throw RuntimeException("upload failed") },
            onSuccess = { successCalled = true }
        )
        advanceUntilIdle()

        assertTrue(successCalled)
        assertNull(viewModel.errorMessage.value)
    }


    @Test
    fun `createPost with no user sets error`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)
        viewModel.loadNewPost()
        viewModel.updateCaption("Hello")
        viewModel.updateLocation("Baščaršija")

        viewModel.createPost(uploadImage = { _, _ -> }, onSuccess = {})
        advanceUntilIdle()

        assertEquals("User not logged in", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `createPost when postApi throws sets error`() = runTest {
        viewModel.loadNewPost()
        viewModel.updateCaption("Hello")
        viewModel.updateLocation("Baščaršija")
        coEvery { postApi.createPost(any(), any(), any()) } throws
                RuntimeException("api down")

        var successCalled = false
        viewModel.createPost(
            uploadImage = { _, _ -> },
            onSuccess = { successCalled = true }
        )
        advanceUntilIdle()

        assertEquals("api down", viewModel.errorMessage.value)
        assertFalse(successCalled)
        assertFalse(viewModel.isLoading.value)
    }


    private fun fakePostResponse(id: String) = PostResponse(
        id = id,
        userId = "user-123",
        imageUrl = null,
        caption = "Hello",
        createdAt = "2026-01-01T10:00:00Z",
        likes = 0
    )
}