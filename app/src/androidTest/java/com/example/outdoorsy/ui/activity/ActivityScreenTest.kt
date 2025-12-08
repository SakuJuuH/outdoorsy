package com.example.outdoorsy.ui.activity

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.outdoorsy.R
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantAnswerDto
import com.example.outdoorsy.domain.model.Activity
import com.example.outdoorsy.domain.model.Location
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ActivityScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun renderWithState(sampleState: ActivityUiState) {
        composeTestRule.setContent {
            WeatherAppTheme {
                ActivityScreenContent(
                    uiState = sampleState,
                    onUpdateActivity = {},
                    onDeleteActivity = {},
                    onUpdateLocation = {},
                    onUpdateShowDialog = {},
                    onUpdateNewActivityName = {},
                    onAddActivity = {},
                    onUpdateStartDateTime = { _, _, _, _ -> },
                    onUpdateEndDateTime = { _, _, _, _ -> },
                    onPerformSearch = {},
                    onNavigateToShop = {}
                )
            }
        }
    }

    @Test
    fun `activity screen displays title and subtitle`() {
        val sampleState = ActivityUiState()
        renderWithState(sampleState)

        composeTestRule.onNodeWithText(context.getString(R.string.activity_screen_title))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.activity_screen_subtitle))
            .assertIsDisplayed()
    }

    @Test
    fun `clicking add activity button shows dialog`() {
        val sampleState = ActivityUiState(showActivityDialog = true)
        renderWithState(sampleState)

        composeTestRule.onNodeWithText(context.getString(R.string.add_activity_title))
            .assertIsDisplayed()
    }

    @Test
    fun `search button enabled only when activity and location selected`() {
        val sampleState = ActivityUiState(
            selectedActivity = Activity(name = "Running"),
            selectedLocation = Location(
                name = "Park",
                country = "FI",
                state = null,
                latitude = 60.294,
                longitude = 25.039
            )
        )
        renderWithState(sampleState)

        composeTestRule.onNodeWithText(context.getString(R.string.search_button_label))
            .assertIsEnabled()
    }

    @Test
    fun `invalid time range shows error message`() {
        val sampleState = ActivityUiState(timeRangeErrorId = R.string.time_error_invalid)
        renderWithState(sampleState)

        composeTestRule.onNodeWithText(context.getString(R.string.time_error_invalid))
            .assertIsDisplayed()
    }

    @Test
    fun `loading indicator is visible when loading`() {
        val sampleState = ActivityUiState(isLoading = true)
        renderWithState(sampleState)

        composeTestRule.onNode(hasTestTag("loading_indicator"))
            .assertIsDisplayed()
    }

    @Test
    fun `generic error shown when search fails`() {
        val sampleState = ActivityUiState(searchPerformed = false)
        renderWithState(sampleState)

        composeTestRule.onNodeWithText(context.getString(R.string.generic_error))
            .assertIsDisplayed()
    }

    @Test
    fun `recommendation cards shown when ai answer available`() {
        val sampleAnswer = AiAssistantAnswerDto(
            location = "Vantaa, FI",
            startDate = "2025-12-07",
            endDate = "2025-12-07",
            startTime = "09:00",
            endTime = "11:00",
            activity = "Running",
            suitabilityLabel = "Good",
            suitabilityScore = 85,
            suitabilityInfo = listOf("Low wind", "Comfortable temperature"),
            clothingTips = listOf("Wear light layers"),
            clothingItems = listOf("Running shoes", "Light jacket"),
            weatherTips = listOf("Possible light drizzle — bring a cap")
        )
        val sampleState = ActivityUiState(searchPerformed = true, aiAnswer = sampleAnswer)
        renderWithState(sampleState)

        composeTestRule.onNodeWithText(context.getString(R.string.suitability))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.weather_tips))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.clothing_tips))
            .assertIsDisplayed()
    }
}

