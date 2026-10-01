package com.emirgasic.forecastfm.feature.map.route

import com.emirgasic.forecastfm.network.route.RouteApi
import com.emirgasic.forecastfm.network.route.RouteRequest
import com.emirgasic.forecastfm.network.route.RouteResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RouteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var routeApi: RouteApi
    private lateinit var viewModel: RouteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        routeApi = mockk(relaxed = true)
        viewModel = RouteViewModel(routeApi = routeApi)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ---------- initial state ----------

    @Test
    fun `initial state is Idle`() {
        assertEquals(RouteState.Idle, viewModel.state.value)
    }

    @Test
    fun `initial mode is WALKING`() {
        assertEquals(TravelMode.WALKING, viewModel.mode.value)
    }

    @Test
    fun `initial actualOrigin is null`() {
        assertNull(viewModel.actualOrigin.value)
    }

    // ---------- setOrigin ----------

    @Test
    fun `setOrigin updates actualOrigin`() {
        viewModel.setOrigin(43.85, 18.41)
        assertEquals(43.85 to 18.41, viewModel.actualOrigin.value)
    }

    // ---------- setMode ----------

    @Test
    fun `setMode with same value does nothing`() = runTest {
        viewModel.setOrigin(43.85, 18.41)
        viewModel.setMode(TravelMode.WALKING) { 43.85 to 18.41 }
        advanceUntilIdle()

        assertEquals(TravelMode.WALKING, viewModel.mode.value)
        coVerify(exactly = 0) { routeApi.getRoute(any()) }
    }

    @Test
    fun `setMode with different value and known destination refetches route`() = runTest {
        coEvery { routeApi.getRoute(any()) } returns fakeRoute()

        viewModel.setOrigin(43.85, 18.41)
        viewModel.fetchRoute({ 43.85 to 18.41 }, 43.86, 18.42)
        advanceUntilIdle()
        coVerify(exactly = 1) { routeApi.getRoute(any()) }

        viewModel.setMode(TravelMode.DRIVING) { 43.85 to 18.41 }
        advanceUntilIdle()

        assertEquals(TravelMode.DRIVING, viewModel.mode.value)
        coVerify(exactly = 2) { routeApi.getRoute(any()) }
    }

    @Test
    fun `setMode with no destination only updates mode`() = runTest {
        viewModel.setMode(TravelMode.DRIVING) { 43.85 to 18.41 }
        advanceUntilIdle()

        assertEquals(TravelMode.DRIVING, viewModel.mode.value)
        coVerify(exactly = 0) { routeApi.getRoute(any()) }
    }

    // ---------- fetchRoute ----------

    @Test
    fun `fetchRoute with known origin calls routeApi and sets Success`() = runTest {
        coEvery { routeApi.getRoute(any()) } returns fakeRoute()

        viewModel.setOrigin(43.85, 18.41)
        viewModel.fetchRoute({ null }, 43.86, 18.42)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is RouteState.Success)
        assertEquals(500.0, (state as RouteState.Success).route.distanceMeters, 0.0)
    }

    @Test
    fun `fetchRoute uses origin from location resolver when not set`() = runTest {
        coEvery { routeApi.getRoute(any()) } returns fakeRoute()

        viewModel.fetchRoute({ 43.85 to 18.41 }, 43.86, 18.42)
        advanceUntilIdle()

        assertEquals(43.85 to 18.41, viewModel.actualOrigin.value)
    }

    @Test
    fun `fetchRoute with null location resolver sets Error`() = runTest {
        viewModel.fetchRoute({ null }, 43.86, 18.42)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is RouteState.Error)
        assertEquals("Could not get your location", (state as RouteState.Error).message)
    }

    @Test
    fun `fetchRoute with repository exception sets Error`() = runTest {
        coEvery { routeApi.getRoute(any()) } throws RuntimeException("api down")

        viewModel.setOrigin(43.85, 18.41)
        viewModel.fetchRoute({ null }, 43.86, 18.42)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is RouteState.Error)
        assertEquals("api down", (state as RouteState.Error).message)
    }

    @Test
    fun `fetchRoute with null exception message uses fallback`() = runTest {
        coEvery { routeApi.getRoute(any()) } throws RuntimeException()

        viewModel.setOrigin(43.85, 18.41)
        viewModel.fetchRoute({ null }, 43.86, 18.42)
        advanceUntilIdle()

        assertEquals("Route failed", (viewModel.state.value as RouteState.Error).message)
    }

    @Test
    fun `fetchRoute sends correct request with walking mode by default`() = runTest {
        val slot = slot<RouteRequest>()
        coEvery { routeApi.getRoute(capture(slot)) } returns fakeRoute()

        viewModel.setOrigin(43.85, 18.41)
        viewModel.fetchRoute({ null }, 43.86, 18.42)
        advanceUntilIdle()

        assertEquals(43.85, slot.captured.fromLat, 0.0)
        assertEquals(18.41, slot.captured.fromLng, 0.0)
        assertEquals(43.86, slot.captured.toLat, 0.0)
        assertEquals(18.42, slot.captured.toLng, 0.0)
        assertEquals("walking", slot.captured.mode)
    }

    @Test
    fun `fetchRoute sends driving mode when mode is DRIVING`() = runTest {
        val slot = slot<RouteRequest>()
        coEvery { routeApi.getRoute(capture(slot)) } returns fakeRoute()

        viewModel.setOrigin(43.85, 18.41)
        viewModel.setMode(TravelMode.DRIVING) { null }
        viewModel.fetchRoute({ null }, 43.86, 18.42)
        advanceUntilIdle()

        assertEquals("driving", slot.captured.mode)
    }

    @Test
    fun `fetchRoute does not call location resolver when origin is known`() = runTest {
        var resolverCalled = false
        coEvery { routeApi.getRoute(any()) } returns fakeRoute()

        viewModel.setOrigin(43.85, 18.41)
        viewModel.fetchRoute({ resolverCalled = true; null }, 43.86, 18.42)
        advanceUntilIdle()

        assertTrue(!resolverCalled)
    }

    // ---------- clearRoute ----------

    @Test
    fun `clearRoute resets state to Idle`() = runTest {
        coEvery { routeApi.getRoute(any()) } returns fakeRoute()
        viewModel.setOrigin(43.85, 18.41)
        viewModel.fetchRoute({ null }, 43.86, 18.42)
        advanceUntilIdle()
        assertTrue(viewModel.state.value is RouteState.Success)

        viewModel.clearRoute()

        assertEquals(RouteState.Idle, viewModel.state.value)
    }

    @Test
    fun `clearRoute clears current destination so setMode does not refetch`() = runTest {
        coEvery { routeApi.getRoute(any()) } returns fakeRoute()
        viewModel.setOrigin(43.85, 18.41)
        viewModel.fetchRoute({ null }, 43.86, 18.42)
        advanceUntilIdle()

        viewModel.clearRoute()
        viewModel.setMode(TravelMode.DRIVING) { null }
        advanceUntilIdle()

        // Only the initial fetch — no refetch after clear
        coVerify(exactly = 1) { routeApi.getRoute(any()) }
    }

    // ---------- helpers ----------

    private fun fakeRoute() = RouteResponse(
        geometry = "abc",
        distanceMeters = 500.0,
        durationSeconds = 300.0
    )
}