package com.example.outdoorsy.ui.weather

import android.util.Log
import com.example.outdoorsy.data.repository.SettingsRepository
import com.example.outdoorsy.domain.model.Location
import com.example.outdoorsy.domain.model.weather.ForecastResponse
import com.example.outdoorsy.domain.model.weather.WeatherResponse
import com.example.outdoorsy.domain.repository.LocationRepository
import com.example.outdoorsy.domain.usecase.GetCurrentWeather
import com.example.outdoorsy.domain.usecase.GetForecast
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.TemperatureSystem
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var viewModel: WeatherViewModel

    @MockK
    private lateinit var getCurrentWeather: GetCurrentWeather

    @MockK
    private lateinit var getForecast: GetForecast

    @MockK
    private lateinit var settingsRepository: SettingsRepository

    @MockK
    private lateinit var locationRepository: LocationRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // MOCK ANDROID LOG
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        // Default Behaviors
        every { settingsRepository.getTemperatureUnit() } returns flowOf(TemperatureSystem.METRIC.code)
        every { settingsRepository.getLanguage() } returns flowOf(AppLanguage.ENGLISH.code)
        every { settingsRepository.getRecentSearches() } returns flowOf(emptyList())

        every { locationRepository.getAllLocations() } returns flowOf(emptyList())
        coEvery { locationRepository.getCurrentLocation() } returns null
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCurrentLocationWeather fetches data successfully when location exists`() = runTest {
        // Given
        val mockLocation = Location(name = "Kerava", latitude = 60.4, longitude = 25.1)
        val mockWeatherResponse = mockk<WeatherResponse>(relaxed = true) {
            every { name } returns "Kerava"
            every { main.temperature } returns 20.0
        }
        val mockForecastResponse = mockk<ForecastResponse>(relaxed = true) {
            every { listOfForecastItems } returns emptyList()
        }

        coEvery { locationRepository.getCurrentLocation() } returns mockLocation

        coEvery {
            getCurrentWeather(
                lat = 60.4,
                lon = 25.1,
                city = any(),
                units = "metric",
                language = "en"
            )
        } returns mockWeatherResponse

        coEvery {
            getForecast(
                lat = 60.4,
                lon = 25.1,
                city = any(),
                units = "metric",
                language = "en"
            )
        } returns mockForecastResponse

        // When
        viewModel = WeatherViewModel(
            getCurrentWeather,
            getForecast,
            settingsRepository,
            locationRepository
        )

        // Collect the flow to activate stateIn(WhileSubscribed)**
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.weatherList.collect()
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val weatherList = viewModel.weatherList.value
        assertTrue("Weather list should not be empty", weatherList.isNotEmpty())
        assertEquals("Kerava", weatherList[0]?.location)
        assertEquals(true, weatherList[0]?.isCurrentLocation)
        assertEquals(20, weatherList[0]?.temp)
    }

    @Test
    fun `searchAndAddLocation saves location when valid city found`() = runTest {
        // Given
        val query = "Tampere"
        val mockLocation = Location(name = "Tampere", latitude = 61.5, longitude = 23.7)

        coEvery { locationRepository.getCoordinatesByLocation("Tampere") } returns mockLocation
        coEvery { locationRepository.saveLocation(mockLocation) } returns Unit
        coEvery { settingsRepository.addRecentSearch("Tampere") } returns Unit

        viewModel = WeatherViewModel(
            getCurrentWeather,
            getForecast,
            settingsRepository,
            locationRepository
        )

        // When
        viewModel.searchAndAddLocation(query)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { locationRepository.saveLocation(mockLocation) }
        coVerify { settingsRepository.addRecentSearch("Tampere") }
    }

    @Test
    fun `searchAndAddLocation does nothing if location not found`() = runTest {
        // Given
        // Matching "Invalidcity" because ViewModel sanitizes input
        coEvery { locationRepository.getCoordinatesByLocation("Invalidcity") } returns null

        viewModel = WeatherViewModel(
            getCurrentWeather,
            getForecast,
            settingsRepository,
            locationRepository
        )

        // When
        viewModel.searchAndAddLocation("InvalidCity")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { locationRepository.saveLocation(any()) }
    }

    @Test
    fun `removeLocation calls delete on repository`() = runTest {
        // Given
        val cityToRemove = "Helsinki"
        coEvery { locationRepository.deleteByName(cityToRemove) } returns Unit

        viewModel = WeatherViewModel(
            getCurrentWeather,
            getForecast,
            settingsRepository,
            locationRepository
        )

        // When
        viewModel.removeLocation(cityToRemove)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { locationRepository.deleteByName(cityToRemove) }
    }

    @Test
    fun `weatherList combines GPS and Saved locations correctly`() = runTest {
        // Given
        val gpsLocation = Location(name = "GPS City", latitude = 10.0, longitude = 10.0)
        val savedLocation = Location(name = "Saved City", latitude = 20.0, longitude = 20.0)

        // 1. Mock Response for GPS (Must have a unique name)
        val gpsResponse = mockk<WeatherResponse>(relaxed = true) {
            every { name } returns "GPS City"
        }
        // 2. Mock Response for Saved (Must have a DIFFERENT unique name)
        val savedResponse = mockk<WeatherResponse>(relaxed = true) {
            every { name } returns "Saved City"
        }

        val mockForecast = mockk<ForecastResponse>(relaxed = true) {
            every { listOfForecastItems } returns emptyList()
        }

        // Setup Repositories
        coEvery { locationRepository.getCurrentLocation() } returns gpsLocation
        every { locationRepository.getAllLocations() } returns flowOf(listOf(savedLocation))


        // Match the call for GPS coordinates (10.0, 10.0)
        coEvery {
            getCurrentWeather(lat = 10.0, lon = 10.0, city = any(), units = any(), language = any())
        } returns gpsResponse

        // Match the call for Saved coordinates (20.0, 20.0)
        coEvery {
            getCurrentWeather(lat = 20.0, lon = 20.0, city = any(), units = any(), language = any())
        } returns savedResponse

        // Forecast can remain generic
        coEvery {
            getForecast(any(), any(), any(), any(), any())
        } returns mockForecast

        // When
        viewModel = WeatherViewModel(
            getCurrentWeather,
            getForecast,
            settingsRepository,
            locationRepository
        )

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.weatherList.collect()
        }

        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val list = viewModel.weatherList.value

        assertEquals("Should have 2 items (1 GPS + 1 Saved)", 2, list.size)
        assertTrue(list.any { it?.isCurrentLocation == true })
        assertTrue(list.any { it?.location == "Saved City" })
    }
}