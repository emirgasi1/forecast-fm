package com.emirgasic.forecastfm.feature.onboarding

import com.emirgasic.forecastfm.core.onboarding.OnboardingPreferences
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
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
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var prefs: OnboardingPreferences
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        prefs = mockk(relaxed = true)
        viewModel = OnboardingViewModel(prefs = prefs)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `initial state has step 1 and empty selections`() {
        val s = viewModel.state.value
        assertEquals(1, s.currentStep)
        assertNull(s.ageGroup)
        assertTrue(s.companions.isEmpty())
        assertTrue(s.weatherPrefs.isEmpty())
        assertTrue(s.musicGenres.isEmpty())
        assertTrue(s.placeCategories.isEmpty())
        assertTrue(s.moods.isEmpty())
    }

    @Test
    fun `totalSteps is 6`() {
        assertEquals(6, viewModel.totalSteps)
    }


    @Test
    fun `setAgeGroup sets the value`() {
        viewModel.setAgeGroup("25–34")
        assertEquals("25–34", viewModel.state.value.ageGroup)
    }

    @Test
    fun `setAgeGroup overwrites previous value`() {
        viewModel.setAgeGroup("18–24")
        viewModel.setAgeGroup("35–49")
        assertEquals("35–49", viewModel.state.value.ageGroup)
    }


    @Test
    fun `toggleCompanion adds value when not present`() {
        viewModel.toggleCompanion("Friends")
        assertTrue("Friends" in viewModel.state.value.companions)
    }

    @Test
    fun `toggleCompanion removes value when already present`() {
        viewModel.toggleCompanion("Friends")
        viewModel.toggleCompanion("Friends")
        assertFalse("Friends" in viewModel.state.value.companions)
    }

    @Test
    fun `toggleCompanion keeps other selections when removing one`() {
        viewModel.toggleCompanion("Friends")
        viewModel.toggleCompanion("Family")
        viewModel.toggleCompanion("Friends")

        val companions = viewModel.state.value.companions
        assertFalse("Friends" in companions)
        assertTrue("Family" in companions)
    }


    @Test
    fun `toggleWeather adds and removes correctly`() {
        viewModel.toggleWeather("Sunny")
        assertTrue("Sunny" in viewModel.state.value.weatherPrefs)

        viewModel.toggleWeather("Sunny")
        assertFalse("Sunny" in viewModel.state.value.weatherPrefs)
    }


    @Test
    fun `toggleMusicGenre adds and removes correctly`() {
        viewModel.toggleMusicGenre("Rock")
        assertTrue("Rock" in viewModel.state.value.musicGenres)

        viewModel.toggleMusicGenre("Rock")
        assertFalse("Rock" in viewModel.state.value.musicGenres)
    }


    @Test
    fun `togglePlaceCategory adds and removes correctly`() {
        viewModel.togglePlaceCategory("Cafes")
        assertTrue("Cafes" in viewModel.state.value.placeCategories)

        viewModel.togglePlaceCategory("Cafes")
        assertFalse("Cafes" in viewModel.state.value.placeCategories)
    }


    @Test
    fun `toggleMood adds and removes correctly`() {
        viewModel.toggleMood("Chill")
        assertTrue("Chill" in viewModel.state.value.moods)

        viewModel.toggleMood("Chill")
        assertFalse("Chill" in viewModel.state.value.moods)
    }


    @Test
    fun `next increments currentStep`() {
        viewModel.next()
        assertEquals(2, viewModel.state.value.currentStep)
    }

    @Test
    fun `next at step 6 does nothing`() {
        repeat(5) { viewModel.next() }  // now at step 6
        assertEquals(6, viewModel.state.value.currentStep)

        viewModel.next()  // should stay at 6
        assertEquals(6, viewModel.state.value.currentStep)
    }

    @Test
    fun `back decrements currentStep`() {
        viewModel.next()
        viewModel.next()
        assertEquals(3, viewModel.state.value.currentStep)

        viewModel.back()
        assertEquals(2, viewModel.state.value.currentStep)
    }

    @Test
    fun `back at step 1 does nothing`() {
        viewModel.back()
        assertEquals(1, viewModel.state.value.currentStep)
    }


    @Test
    fun `step 1 valid only when ageGroup is set`() {
        assertFalse(viewModel.isCurrentStepValid())
        viewModel.setAgeGroup("25–34")
        assertTrue(viewModel.isCurrentStepValid())
    }

    @Test
    fun `step 2 valid only when companions non empty`() {
        viewModel.next()
        assertFalse(viewModel.isCurrentStepValid())
        viewModel.toggleCompanion("Friends")
        assertTrue(viewModel.isCurrentStepValid())
    }

    @Test
    fun `step 3 valid only when weatherPrefs non empty`() {
        viewModel.next(); viewModel.next()
        assertFalse(viewModel.isCurrentStepValid())
        viewModel.toggleWeather("Sunny")
        assertTrue(viewModel.isCurrentStepValid())
    }

    @Test
    fun `step 4 valid only when musicGenres non empty`() {
        repeat(3) { viewModel.next() }
        assertFalse(viewModel.isCurrentStepValid())
        viewModel.toggleMusicGenre("Rock")
        assertTrue(viewModel.isCurrentStepValid())
    }

    @Test
    fun `step 5 valid only when placeCategories non empty`() {
        repeat(4) { viewModel.next() }
        assertFalse(viewModel.isCurrentStepValid())
        viewModel.togglePlaceCategory("Cafes")
        assertTrue(viewModel.isCurrentStepValid())
    }

    @Test
    fun `step 6 valid only when moods non empty`() {
        repeat(5) { viewModel.next() }
        assertFalse(viewModel.isCurrentStepValid())
        viewModel.toggleMood("Chill")
        assertTrue(viewModel.isCurrentStepValid())
    }


    @Test
    fun `finish saves all selections and calls onDone`() = runTest {
        viewModel.setAgeGroup("25–34")
        viewModel.toggleCompanion("Friends")
        viewModel.toggleWeather("Sunny")
        viewModel.toggleMusicGenre("Rock")
        viewModel.togglePlaceCategory("Cafes")
        viewModel.toggleMood("Chill")

        var doneCalled = false
        viewModel.finish { doneCalled = true }

        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.setAgeGroup("25–34") }
        coVerify(exactly = 1) { prefs.setCompanions(setOf("Friends")) }
        coVerify(exactly = 1) { prefs.setWeatherPrefs(setOf("Sunny")) }
        coVerify(exactly = 1) { prefs.setMusicGenres(setOf("Rock")) }
        coVerify(exactly = 1) { prefs.setPlaceCategories(setOf("Cafes")) }
        coVerify(exactly = 1) { prefs.setMoods(setOf("Chill")) }
        coVerify(exactly = 1) { prefs.markCompleted() }

        assertTrue(doneCalled)
    }

    @Test
    fun `finish without ageGroup does not call setAgeGroup but still completes`() = runTest {
        // ageGroup left null on purpose
        viewModel.toggleCompanion("Friends")

        var doneCalled = false
        viewModel.finish { doneCalled = true }

        advanceUntilIdle()

        coVerify(exactly = 0) { prefs.setAgeGroup(any()) }
        coVerify(exactly = 1) { prefs.setCompanions(any()) }
        coVerify(exactly = 1) { prefs.markCompleted() }

        assertTrue(doneCalled)
    }


    @Test
    fun `skip marks completed and calls onDone without saving any selections`() = runTest {
        var doneCalled = false
        viewModel.skip { doneCalled = true }

        advanceUntilIdle()

        coVerify(exactly = 1) { prefs.markCompleted() }
        coVerify(exactly = 0) { prefs.setAgeGroup(any()) }
        coVerify(exactly = 0) { prefs.setCompanions(any()) }
        coVerify(exactly = 0) { prefs.setWeatherPrefs(any()) }
        coVerify(exactly = 0) { prefs.setMusicGenres(any()) }
        coVerify(exactly = 0) { prefs.setPlaceCategories(any()) }
        coVerify(exactly = 0) { prefs.setMoods(any()) }

        assertTrue(doneCalled)
    }
}