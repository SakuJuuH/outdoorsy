package com.example.outdoorsy.data.repository


import com.example.outdoorsy.data.remote.WeatherApiService
import com.example.outdoorsy.data.remote.dto.weather.CloudsDto
import com.example.outdoorsy.data.remote.dto.weather.CoordDto
import com.example.outdoorsy.data.remote.dto.weather.MainDto
import com.example.outdoorsy.data.remote.dto.weather.WeatherDto
import com.example.outdoorsy.data.remote.dto.weather.WeatherResponseDto
import com.example.outdoorsy.data.remote.dto.weather.WeatherSysDto
import com.example.outdoorsy.data.remote.dto.weather.WindDto
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

    private lateinit var repository: WeatherRepositoryImpl

    @MockK
    private lateinit var mockWeatherApi: WeatherApiService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = WeatherRepositoryImpl(mockWeatherApi)
    }

    @Test
    fun `getCurrentWeatherByCity calls api and maps to domain`() = runTest {
        // Given
        val city = "London"
        val units = "metric"
        val lang = "en"
        val mockDto = createMockWeatherResponseDto(name = "London", temp = 15.0)

        // Mock the API call
        coEvery {
            mockWeatherApi.getCurrentWeatherByCity(city, units, lang)
        } returns mockDto

        // When
        val result = repository.getCurrentWeatherByCity(city, units, lang)

        // Then
        // 1. Verify the API was called with exact parameters
        coVerify { mockWeatherApi.getCurrentWeatherByCity(city, units, lang) }

        // 2. Verify the result is the Domain Model (not the DTO)
        assertEquals("London", result.name)
        assertEquals(15.0, result.main.temperature, 0.0)
        assertEquals("Clouds", result.weather.first().group)
    }

    @Test
    fun `getCurrentWeatherByCoordinates calls api and maps to domain`() = runTest {
        // Given
        val lat = 51.5
        val lon = -0.1
        val units = "imperial"
        val lang = "en"
        val mockDto = createMockWeatherResponseDto(name = "Coordinates Location", temp = 60.0)

        // Mock the API call
        coEvery {
            mockWeatherApi.getCurrentWeatherByCoordinates(lat, lon, units, lang)
        } returns mockDto

        // When
        val result = repository.getCurrentWeatherByCoordinates(lat, lon, units, lang)

        // Then
        // 1. Verify the API was called with exact parameters
        coVerify { mockWeatherApi.getCurrentWeatherByCoordinates(lat, lon, units, lang) }

        // 2. Verify mapping
        assertEquals("Coordinates Location", result.name)
        assertEquals(60.0, result.main.temperature, 0.0)
    }

    private fun createMockWeatherResponseDto(name: String, temp: Double): WeatherResponseDto {
        return WeatherResponseDto(
            coord = CoordDto(0.0, 0.0),
            weather = listOf(
                WeatherDto(id = 800, main = "Clouds", description = "scattered clouds", icon = "03d")
            ),
            base = "stations",
            main = MainDto(
                temp = temp,
                feelsLike = temp,
                tempMin = temp,
                tempMax = temp,
                pressure = 1012,
                humidity = 80,
                seaLevel = 1012,
                grndLevel = 1000
            ),
            visibility = 10000,
            wind = WindDto(speed = 5.0, deg = 200, gust = 0.0),
            clouds = CloudsDto(all = 20),
            dt = 1600000000L,
            sys = WeatherSysDto(type = 1, id = 123, country = "GB", sunrise = 1600000000L, sunset = 1600040000L),
            timezone = 3600,
            id = 2643743,
            name = name,
            cod = 200
        )
    }
}