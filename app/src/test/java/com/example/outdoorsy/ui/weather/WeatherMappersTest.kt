package com.example.outdoorsy.ui.weather


import com.example.outdoorsy.domain.model.weather.Clouds
import com.example.outdoorsy.domain.model.weather.Coord
import com.example.outdoorsy.domain.model.weather.ForecastItem
import com.example.outdoorsy.domain.model.weather.ForecastSys
import com.example.outdoorsy.domain.model.weather.Main
import com.example.outdoorsy.domain.model.weather.PartOfDay
import com.example.outdoorsy.domain.model.weather.Weather
import com.example.outdoorsy.domain.model.weather.WeatherResponse
import com.example.outdoorsy.domain.model.weather.WeatherSys
import com.example.outdoorsy.domain.model.weather.Wind
import com.example.outdoorsy.ui.weather.mappers.toUiModel
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class WeatherMappersTest {

    @Test
    fun `toUiModel maps basic fields correctly`() {
        // Given
        val mockWeatherResponse = createMockWeatherResponse(
            temp = 25.6, // Should round to 25 (toInt truncates) or be handled as Int
            description = "clear sky"
        )
        val mockForecast = emptyList<ForecastItem>()

        // When
        val uiModel = mockWeatherResponse.toUiModel(mockForecast, "metric")

        // Then
        assertEquals("Test City", uiModel.location)
        assertEquals(25, uiModel.temp) // 25.6.toInt() is 25
        assertEquals("Clear sky", uiModel.condition) // Should capitalize
        assertEquals(10, uiModel.humidity)
        assertEquals(5, uiModel.windSpeed)
    }

    @Test
    fun `toDailyForecasts groups items by day and calculates high low`() {
        // Given
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        // Reset to noon to avoid timezone edge cases in simple tests
        calendar.set(Calendar.HOUR_OF_DAY, 12)

        val todaySec = calendar.timeInMillis / 1000
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowSec = calendar.timeInMillis / 1000

        val forecastItems = listOf(
            // Today: High 20, Low 10
            createMockForecastItem(todaySec, 10.0),
            createMockForecastItem(todaySec, 20.0),

            // Tomorrow: High 15, Low 5
            createMockForecastItem(tomorrowSec, 15.0),
            createMockForecastItem(tomorrowSec, 5.0)
        )

        val mockWeatherResponse = createMockWeatherResponse(20.0, "clouds")

        // When
        val uiModel = mockWeatherResponse.toUiModel(forecastItems, "metric")

        // Then
        val dailyForecast = uiModel.forecast
        assertEquals(2, dailyForecast.size)

        // Verify Day 1
        assertEquals(20, dailyForecast[0].high)
        assertEquals(10, dailyForecast[0].low)

        // Verify Day 2
        assertEquals(15, dailyForecast[1].high)
        assertEquals(5, dailyForecast[1].low)
    }

    // --- Helpers to create dummy data ---

    private fun createMockWeatherResponse(temp: Double, description: String): WeatherResponse {
        return WeatherResponse(
            coord = Coord(0.0, 0.0),
            weather = listOf(Weather(800, "Clear", description, "01d")),
            main = Main(temp, temp, temp, temp, 1013, 0, 0, 10),
            visibility = 10000,
            wind = Wind(5.5, 0, 0.0),
            clouds = Clouds(0),
            timeOfData = 1600000000,
            sys = WeatherSys("FI", 1600000000, 1600040000),
            timezone = 7200,
            name = "Test City"
        )
    }

    private fun createMockForecastItem(time: Long, temp: Double): ForecastItem {
        return ForecastItem(
            timeOfData = time.toInt(),
            main = Main(temp, temp, temp, temp, 1013, 0, 0, 50),
            weather = listOf(Weather(800, "Clear", "clear sky", "01d")),
            wind = Wind(5.0, 0, 0.0),
            clouds = Clouds(0),
            pop = 0.0,
            rain = null,
            sys = ForecastSys(PartOfDay.DAY)
        )
    }
}