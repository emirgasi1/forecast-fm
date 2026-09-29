package com.emirgasic.forecastfm.feature.settings.notifications

import com.emirgasic.forecastfm.core.notifications.NotificationPreferences
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var prefs: NotificationPreferences

    private lateinit var weatherFlow: MutableStateFlow<Boolean>
    private lateinit var playlistFlow: MutableStateFlow<Boolean>
    private lateinit var outfitFlow: MutableStateFlow<Boolean>
    private lateinit var friendFlow: MutableStateFlow<Boolean>

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        prefs = mockk(relaxed = true)

        weatherFlow = MutableStateFlow(true)
        playlistFlow = MutableStateFlow(true)
        outfitFlow = MutableStateFlow(true)
        friendFlow = MutableStateFlow(true)

        every { prefs.weatherEnabled } returns weatherFlow
        every { prefs.playlistEnabled } returns playlistFlow
        every { prefs.outfitEnabled } returns outfitFlow
        every { prefs.friendEnabled } returns friendFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = NotificationsViewModel(prefs)

    // ---------- initial collector behavior ----------

    @Test
    fun `all toggles default to true when prefs emit true`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        assertTrue(vm.weather.value)
        assertTrue(vm.playlist.value)
        assertTrue(vm.outfit.value)
        assertTrue(vm.friend.value)
    }

    @Test
    fun `all toggles reflect false when prefs emit false`() = runTest {
        weatherFlow.value = false
        playlistFlow.value = false
        outfitFlow.value = false
        friendFlow.value = false

        val vm = createViewModel()
        advanceUntilIdle()

        assertFalse(vm.weather.value)
        assertFalse(vm.playlist.value)
        assertFalse(vm.outfit.value)
        assertFalse(vm.friend.value)
    }

    @Test
    fun `each toggle reflects its own pref independently`() = runTest {
        weatherFlow.value = false
        playlistFlow.value = true
        outfitFlow.value = false
        friendFlow.value = true

        val vm = createViewModel()
        advanceUntilIdle()

        assertFalse(vm.weather.value)
        assertTrue(vm.playlist.value)
        assertFalse(vm.outfit.value)
        assertTrue(vm.friend.value)
    }

    @Test
    fun `toggle updates when prefs flow emits new value after construction`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()
        assertTrue(vm.weather.value)

        weatherFlow.value = false
        advanceUntilIdle()

        assertFalse(vm.weather.value)
    }

    @Test
    fun `toggle reverts when prefs flow emits true again`() = runTest {
        weatherFlow.value = false
        val vm = createViewModel()
        advanceUntilIdle()
        assertFalse(vm.weather.value)

        weatherFlow.value = true
        advanceUntilIdle()

        assertTrue(vm.weather.value)
    }

    // ---------- setters ----------

    @Test
    fun `setWeather calls prefs setWeather`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.setWeather(false)
        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.setWeather(false) }
    }

    @Test
    fun `setPlaylist calls prefs setPlaylist`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.setPlaylist(false)
        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.setPlaylist(false) }
    }

    @Test
    fun `setOutfit calls prefs setOutfit`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.setOutfit(false)
        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.setOutfit(false) }
    }

    @Test
    fun `setFriend calls prefs setFriend`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.setFriend(false)
        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.setFriend(false) }
    }

    @Test
    fun `each setter only calls its own pref`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.setWeather(false)
        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.setWeather(false) }
        coVerify(exactly = 0) { prefs.setPlaylist(any()) }
        coVerify(exactly = 0) { prefs.setOutfit(any()) }
        coVerify(exactly = 0) { prefs.setFriend(any()) }
    }

    @Test
    fun `setter accepts both true and false`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.setWeather(true)
        vm.setWeather(false)
        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.setWeather(true) }
        coVerify(exactly = 1) { prefs.setWeather(false) }
    }

    @Test
    fun `calling all four setters in sequence invokes each pref exactly once`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.setWeather(false)
        vm.setPlaylist(false)
        vm.setOutfit(false)
        vm.setFriend(false)
        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.setWeather(false) }
        coVerify(exactly = 1) { prefs.setPlaylist(false) }
        coVerify(exactly = 1) { prefs.setOutfit(false) }
        coVerify(exactly = 1) { prefs.setFriend(false) }
    }
}