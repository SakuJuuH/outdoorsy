package com.example.outdoorsy.ui.weather.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.ui.weather.model.DailyForecast
import com.example.outdoorsy.ui.weather.model.WeatherData
import org.junit.Rule
import org.junit.Test

class WeatherComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weatherCard_displaysCorrectInfo() {
        // Given
        val sampleWeather = WeatherData(
            location = "London",
            temp = 15,
            condition = "Cloudy",
            high = 18,
            low = 12,
            humidity = 70,
            windSpeed = 5,
            visibility = 10.0,
            pressure = 1012.0,
            sunrise = "6:00 AM",
            sunset = "8:00 PM",
            forecast = emptyList(),
            icon = "04d",
            isCurrentLocation = true,
            unit = "metric"
        )

        // When
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherCard(
                    weatherData = sampleWeather,
                    onRemoveClick = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("London").assertIsDisplayed()
        composeTestRule.onNodeWithText("15°C").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cloudy").assertIsDisplayed()
        composeTestRule.onNodeWithText("H: 18°").assertIsDisplayed()
        composeTestRule.onNodeWithText("L: 12°").assertIsDisplayed()
    }

    @Test
    fun weatherCard_showsRemoveButton_whenNotCurrentLocation() {
        // Given
        var removeClicked = false
        val sampleWeather = WeatherData(
            location = "Paris",
            temp = 20,
            condition = "Sunny",
            high = 25,
            low = 15,
            humidity = 50,
            windSpeed = 10,
            visibility = 10.0,
            pressure = 1015.0,
            sunrise = "6:00 AM",
            sunset = "8:00 PM",
            forecast = emptyList(),
            icon = "01d",
            isCurrentLocation = false, // Should show remove button
            unit = "metric"
        )

        // When
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherCard(
                    weatherData = sampleWeather,
                    onRemoveClick = { removeClicked = true }
                )
            }
        }

        // Then
        // The content description for the remove button (defined in WeatherCard.kt)
        composeTestRule.onNodeWithContentDescription("Remove location").assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Remove location").performClick()
        assert(removeClicked)
    }

    @Test
    fun weatherDetailCard_displaysLabelAndValue() {
        // When
        composeTestRule.setContent {
            WeatherAppTheme {
                WeatherDetailCard(
                    icon = Icons.Default.WaterDrop,
                    label = "Humidity",
                    value = "65%"
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Humidity").assertIsDisplayed()
        composeTestRule.onNodeWithText("65%").assertIsDisplayed()
    }

    @Test
    fun forecastCard_displaysDailyItems() {
        // Given
        val forecastList = listOf(
            DailyForecast("Mon", 20, 15, "Rainy", "10d"),
            DailyForecast("Tue", 22, 16, "Cloudy", "03d")
        )

        // When
        composeTestRule.setContent {
            WeatherAppTheme {
                ForecastCard(forecast = forecastList)
            }
        }

        // Then
        // Verify Day 1
        composeTestRule.onNodeWithText("Mon").assertIsDisplayed()
        composeTestRule.onNodeWithText("20°").assertIsDisplayed() // High
        composeTestRule.onNodeWithText("15°").assertIsDisplayed() // Low
        composeTestRule.onNodeWithText("Rainy").assertIsDisplayed()

        // Verify Day 2
        composeTestRule.onNodeWithText("Tue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cloudy").assertIsDisplayed()
    }
}