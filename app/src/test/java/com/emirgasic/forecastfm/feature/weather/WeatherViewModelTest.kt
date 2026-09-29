package com.emirgasic.forecastfm.feature.weather

import com.emirgasic.forecastfm.data.model.Forecast
import com.emirgasic.forecastfm.data.model.Weather
import com.emirgasic.forecastfm.data.repository.WeatherData
import com.emirgasic.forecastfm.data.repository.WeatherRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: WeatherRepository
    private lateinit var viewModel: WeatherViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = WeatherViewModel(repository = repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `initial uiState is LOADING`() {
        assertEquals(WeatherUiState.LOADING, viewModel.uiState.value)
    }

    @Test
    fun `initial weather is null`() {
        assertNull(viewModel.weather.value)
    }

    @Test
    fun `initial hourly and daily forecasts are empty`() {
        assertTrue(viewModel.hourlyForecast.value.isEmpty())
        assertTrue(viewModel.dailyForecast.value.isEmpty())
    }


    @Test
    fun `loadWeather success sets SUCCESS state and populates data`() = runTest {
        coEvery {
            repository.getWeather("Sarajevo", 43.8563, 18.4131)
        } returns fakeWeatherData()

        viewModel.loadWeather("Sarajevo", 43.8563, 18.4131)
        advanceUntilIdle()

        assertEquals(WeatherUiState.SUCCESS, viewModel.uiState.value)
        assertEquals("Sunny", viewModel.weather.value?.condition)
        assertEquals("Sarajevo", viewModel.weather.value?.location)
    }

    @Test
    fun `loadWeather success sets hourly forecast`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns
                fakeWeatherData(hourlySize = 5, dailySize = 0)

        viewModel.loadWeather("Sarajevo", 43.0, 18.0)
        advanceUntilIdle()

        assertEquals(5, viewModel.hourlyForecast.value.size)
    }

    @Test
    fun `loadWeather success sets daily forecast`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns
                fakeWeatherData(hourlySize = 0, dailySize = 5)

        viewModel.loadWeather("Sarajevo", 43.0, 18.0)
        advanceUntilIdle()

        assertEquals(5, viewModel.dailyForecast.value.size)
    }

    @Test
    fun `loadWeather passes arguments to repository`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns fakeWeatherData()

        viewModel.loadWeather("Mostar", 43.3438, 17.8078)
        advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.getWeather("Mostar", 43.3438, 17.8078)
        }
    }


    @Test
    fun `loadWeather failure sets ERROR state`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } throws
                RuntimeException("network down")

        viewModel.loadWeather("Sarajevo", 43.0, 18.0)
        advanceUntilIdle()

        assertEquals(WeatherUiState.ERROR, viewModel.uiState.value)
    }

    @Test
    fun `loadWeather failure does not populate weather`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } throws
                RuntimeException("boom")

        viewModel.loadWeather("Sarajevo", 43.0, 18.0)
        advanceUntilIdle()

        assertNull(viewModel.weather.value)
        assertTrue(viewModel.hourlyForecast.value.isEmpty())
        assertTrue(viewModel.dailyForecast.value.isEmpty())
    }


    @Test
    fun `loadWeather can be called twice and succeeds on second call`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } throws
                RuntimeException("first fail")

        viewModel.loadWeather("Sarajevo", 43.0, 18.0)
        advanceUntilIdle()
        assertEquals(WeatherUiState.ERROR, viewModel.uiState.value)

        coEvery { repository.getWeather(any(), any(), any()) } returns fakeWeatherData()
        viewModel.loadWeather("Sarajevo", 43.0, 18.0)
        advanceUntilIdle()

        assertEquals(WeatherUiState.SUCCESS, viewModel.uiState.value)
        assertEquals("Sunny", viewModel.weather.value?.condition)
    }

    @Test
    fun `loadWeather calls repository once per invocation`() = runTest {
        coEvery { repository.getWeather(any(), any(), any()) } returns fakeWeatherData()

        viewModel.loadWeather("Sarajevo", 43.0, 18.0)
        advanceUntilIdle()
        viewModel.loadWeather("Sarajevo", 43.0, 18.0)
        advanceUntilIdle()

        coVerify(exactly = 2) { repository.getWeather(any(), any(), any()) }
    }


    private fun fakeWeatherData(
        hourlySize: Int = 3,
        dailySize: Int = 5
    ) = WeatherData(
        weather = Weather(
            location = "Sarajevo",
            temperature = "20°C",
            condition = "Sunny",
            feelsLike = "21°C",
            humidity = "50%",
            wind = "5 km/h",
            uvIndex = "3",
            airQuality = "Good",
            icon = 0
        ),
        hourly = List(hourlySize) {
            Forecast(time = "$it:00", icon = 0, temperature = "20°C")
        },
        daily = List(dailySize) {
            Forecast(time = "Day $it", icon = 0, temperature = "20°C")
        }
    )
}