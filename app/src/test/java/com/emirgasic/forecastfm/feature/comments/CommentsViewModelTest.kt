package com.emirgasic.forecastfm.feature.comments

import com.emirgasic.forecastfm.core.datastore.TokenManager
import com.emirgasic.forecastfm.data.repository.CommentRepository
import com.emirgasic.forecastfm.network.comment.CommentResponse
import com.emirgasic.forecastfm.network.user.UserResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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
class CommentsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var tokenManager: TokenManager
    private lateinit var commentRepository: CommentRepository
    private lateinit var viewModel: CommentsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        tokenManager = mockk()
        commentRepository = mockk(relaxed = true)
        every { tokenManager.getUserId() } returns flowOf("user-123")
        viewModel = CommentsViewModel(
            tokenManager = tokenManager,
            commentRepository = commentRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial comments list is empty`() {
        assertTrue(viewModel.comments.value.isEmpty())
    }

    @Test
    fun `initial likedComments is empty`() {
        assertTrue(viewModel.likedComments.value.isEmpty())
    }

    // ---------- loadComments ----------

    @Test
    fun `loadComments maps responses to Comment domain objects`() = runTest {
        coEvery { commentRepository.getComments("post-1") } returns listOf(
            commentResponse("c1", userId = "u1", text = "Hello")
        )
        coEvery { commentRepository.getUserForComment("u1") } returns
                userResponse("u1", "alice")

        viewModel.loadComments("post-1")
        advanceUntilIdle()

        val comments = viewModel.comments.value
        assertEquals(1, comments.size)
        assertEquals("c1", comments.first().id)
        assertEquals("Hello", comments.first().text)
        assertEquals("alice", comments.first().user.username)
    }

    @Test
    fun `loadComments fetches user info for each comment`() = runTest {
        coEvery { commentRepository.getComments(any()) } returns listOf(
            commentResponse("c1", userId = "u1", text = "A"),
            commentResponse("c2", userId = "u2", text = "B")
        )
        coEvery { commentRepository.getUserForComment("u1") } returns userResponse("u1", "alice")
        coEvery { commentRepository.getUserForComment("u2") } returns userResponse("u2", "bob")

        viewModel.loadComments("post-1")
        advanceUntilIdle()

        coVerify(exactly = 1) { commentRepository.getUserForComment("u1") }
        coVerify(exactly = 1) { commentRepository.getUserForComment("u2") }

        val comments = viewModel.comments.value
        assertEquals("alice", comments[0].user.username)
        assertEquals("bob", comments[1].user.username)
    }

    @Test
    fun `loadComments falls back to 'User' when user fetch fails`() = runTest {
        coEvery { commentRepository.getComments(any()) } returns listOf(
            commentResponse("c1", userId = "u1")
        )
        coEvery { commentRepository.getUserForComment("u1") } throws
                RuntimeException("user not found")

        viewModel.loadComments("post-1")
        advanceUntilIdle()

        assertEquals("User", viewModel.comments.value.first().user.username)
    }

    @Test
    fun `loadComments falls back to 'User' when user response is null`() = runTest {
        coEvery { commentRepository.getComments(any()) } returns listOf(
            commentResponse("c1", userId = "u1")
        )
        coEvery { commentRepository.getUserForComment("u1") } returns null

        viewModel.loadComments("post-1")
        advanceUntilIdle()

        assertEquals("User", viewModel.comments.value.first().user.username)
    }

    @Test
    fun `loadComments uses picsum fallback when profileImageUrl is null`() = runTest {
        coEvery { commentRepository.getComments(any()) } returns listOf(
            commentResponse("c1", userId = "u1")
        )
        coEvery { commentRepository.getUserForComment("u1") } returns
                userResponse("u1", "alice", profileImageUrl = null)

        viewModel.loadComments("post-1")
        advanceUntilIdle()

        assertTrue(viewModel.comments.value.first().user.profileImage.contains("picsum.photos"))
    }

    @Test
    fun `loadComments sets empty list when repository throws`() = runTest {
        coEvery { commentRepository.getComments(any()) } throws
                RuntimeException("api down")

        viewModel.loadComments("post-1")
        advanceUntilIdle()

        assertTrue(viewModel.comments.value.isEmpty())
    }

    @Test
    fun `loadComments with empty response leaves comments empty`() = runTest {
        coEvery { commentRepository.getComments(any()) } returns emptyList()

        viewModel.loadComments("post-1")
        advanceUntilIdle()

        assertTrue(viewModel.comments.value.isEmpty())
    }

    // ---------- addComment ----------

    @Test
    fun `addComment with blank text does nothing`() = runTest {
        viewModel.addComment("post-1", "   ")
        advanceUntilIdle()

        coVerify(exactly = 0) { commentRepository.createCommentWithUserId(any(), any(), any()) }
        assertTrue(viewModel.comments.value.isEmpty())
    }

    @Test
    fun `addComment with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)

        viewModel.addComment("post-1", "Hello")
        advanceUntilIdle()

        coVerify(exactly = 0) { commentRepository.createCommentWithUserId(any(), any(), any()) }
    }

    @Test
    fun `addComment appends new comment to list on success`() = runTest {
        coEvery {
            commentRepository.createCommentWithUserId("user-123", "post-1", "Hello")
        } returns commentResponse("new-c", userId = "user-123", text = "Hello")
        coEvery { commentRepository.getUserForComment("user-123") } returns
                userResponse("user-123", "alice")

        viewModel.addComment("post-1", "Hello")
        advanceUntilIdle()

        val comments = viewModel.comments.value
        assertEquals(1, comments.size)
        assertEquals("new-c", comments.first().id)
        assertEquals("Hello", comments.first().text)
    }

    @Test
    fun `addComment does not append when repository throws`() = runTest {
        coEvery { commentRepository.createCommentWithUserId(any(), any(), any()) } throws
                RuntimeException("api down")

        viewModel.addComment("post-1", "Hello")
        advanceUntilIdle()

        assertTrue(viewModel.comments.value.isEmpty())
    }

    // ---------- toggleLike ----------

    @Test
    fun `toggleLike on unliked comment adds to likedComments and updates count`() = runTest {
        seedCommentsWithOne("c1", likes = 5)

        coEvery { commentRepository.likeComment("c1", "user-123") } returns 6

        viewModel.toggleLike("c1")
        advanceUntilIdle()

        assertTrue("c1" in viewModel.likedComments.value)
        assertEquals(6, viewModel.comments.value.first().likes)
    }

    @Test
    fun `toggleLike on already liked comment removes and updates count`() = runTest {
        seedCommentsWithOne("c1", likes = 5)

        coEvery { commentRepository.likeComment("c1", "user-123") } returns 6
        viewModel.toggleLike("c1")
        advanceUntilIdle()
        assertTrue("c1" in viewModel.likedComments.value)

        coEvery { commentRepository.unlikeComment("c1", "user-123") } returns 5
        viewModel.toggleLike("c1")
        advanceUntilIdle()

        assertFalse("c1" in viewModel.likedComments.value)
        assertEquals(5, viewModel.comments.value.first().likes)
    }

    @Test
    fun `toggleLike with no user does nothing`() = runTest {
        every { tokenManager.getUserId() } returns flowOf(null)
        seedCommentsWithOne("c1", likes = 5)

        viewModel.toggleLike("c1")
        advanceUntilIdle()

        coVerify(exactly = 0) { commentRepository.likeComment(any(), any()) }
        coVerify(exactly = 0) { commentRepository.unlikeComment(any(), any()) }
    }

    @Test
    fun `toggleLike with repository exception leaves state unchanged`() = runTest {
        seedCommentsWithOne("c1", likes = 5)
        coEvery { commentRepository.likeComment(any(), any()) } throws
                RuntimeException("api down")

        viewModel.toggleLike("c1")
        advanceUntilIdle()

        assertFalse("c1" in viewModel.likedComments.value)
        assertEquals(5, viewModel.comments.value.first().likes)
    }

    // ---------- helpers ----------

    private fun TestScope.seedCommentsWithOne(id: String, likes: Int) {
        coEvery { commentRepository.getComments(any()) } returns listOf(
            commentResponse(id, userId = "u1", likes = likes)
        )
        coEvery { commentRepository.getUserForComment("u1") } returns userResponse("u1", "alice")
        viewModel.loadComments("post-1")
        advanceUntilIdle()
    }

    private fun commentResponse(
        id: String,
        userId: String = "u1",
        text: String = "text",
        likes: Int = 0
    ) = CommentResponse(
        id = id,
        userId = userId,
        postId = "post-1",
        text = text,
        createdAt = "2026-01-01T10:00:00Z",
        likes = likes
    )

    private fun userResponse(
        id: String,
        username: String,
        profileImageUrl: String? = "/avatar.jpg"
    ) = UserResponse(
        id = id,
        username = username,
        bio = "bio",
        profileImageUrl = profileImageUrl,
        favoriteLocation = "Sarajevo"
    )
}