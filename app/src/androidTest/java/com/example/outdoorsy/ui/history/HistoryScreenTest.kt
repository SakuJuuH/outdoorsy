package com.example.outdoorsy.ui.history

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToNodeAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.test.platform.app.InstrumentationRegistry
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.history.model.ActivityHistoryItem
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class HistoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    private fun renderWithItems(items: List<ActivityHistoryItem>) {
        composeTestRule.setContent {
            WeatherAppTheme {
                HistoryScreenContent(historyItems = items)
            }
        }
    }

    private fun createSampleHistoryItem(
        activityName: String = "Running",
        location: String = "Helsinki, FI",
        city: String = "Helsinki",
        state: String = "FI",
        timeRange: String = "9:00 AM - 10:00 AM",
        date: String = "Dec 6, 2025",
        suitabilityLabel: String = "Excellent",
        suitabilityScore: Int = 5,
        startDateTime: LocalDateTime = LocalDateTime.of(2025, 12, 6, 9, 0)
    ) = ActivityHistoryItem(
        activityName = activityName,
        activityIcon = Icons.Outlined.Terrain,
        location = location,
        city = city,
        state = state,
        timeRange = timeRange,
        date = date,
        suitabilityLabel = suitabilityLabel,
        suitabilityScore = suitabilityScore,
        startDateTime = startDateTime
    )

    @Test
    fun `history screen displays title and subtitle`() {
        renderWithItems(emptyList())

        composeTestRule.onNodeWithText(context.getString(R.string.activity_history))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.view_previous_activity_search))
            .assertIsDisplayed()
    }

    @Test
    fun `history screen displays empty state when no items`() {
        renderWithItems(emptyList())

        composeTestRule.onNodeWithText(context.getString(R.string.no_activity_history))
            .assertIsDisplayed()
    }

    @Test
    fun `history screen displays activity name when items exist`() {
        val items = listOf(createSampleHistoryItem(activityName = "Running"))
        renderWithItems(items)

        composeTestRule.onNodeWithText("Running")
            .assertIsDisplayed()
    }

    @Test
    fun `history screen displays location for history item`() {
        val items = listOf(createSampleHistoryItem(location = "Helsinki, FI"))
        renderWithItems(items)

        composeTestRule.onNodeWithText("Helsinki, FI")
            .assertIsDisplayed()
    }

    @Test
    fun `history screen displays time range for history item`() {
        val items = listOf(createSampleHistoryItem(timeRange = "9:00 AM - 10:00 AM"))
        renderWithItems(items)

        composeTestRule.onNodeWithText("9:00 AM - 10:00 AM")
            .assertIsDisplayed()
    }

    @Test
    fun `history screen displays date for history item`() {
        val items = listOf(createSampleHistoryItem(date = "Dec 6, 2025"))
        renderWithItems(items)

        composeTestRule.onNodeWithText("Dec 6, 2025")
            .assertIsDisplayed()
    }

    @Test
    fun `history screen displays suitability label`() {
        val items = listOf(createSampleHistoryItem(suitabilityLabel = "Excellent"))
        renderWithItems(items)

        composeTestRule.onNodeWithText("Excellent")
            .assertIsDisplayed()
    }

    @Test
    fun `history screen displays multiple items`() {
        val items = listOf(
            createSampleHistoryItem(
                activityName = "Running",
                startDateTime = LocalDateTime.of(2025, 12, 6, 9, 0)
            ),
            createSampleHistoryItem(
                activityName = "Hiking",
                startDateTime = LocalDateTime.of(2025, 12, 5, 14, 0)
            ),
            createSampleHistoryItem(
                activityName = "Cycling",
                startDateTime = LocalDateTime.of(2025, 12, 4, 10, 0)
            )
        )
        renderWithItems(items)

        composeTestRule.onNodeWithText("Running")
            .assertIsDisplayed()

        // Scroll to find other items
        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("Hiking"))
        composeTestRule.onNodeWithText("Hiking")
            .assertIsDisplayed()

        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("Cycling"))
        composeTestRule.onNodeWithText("Cycling")
            .assertIsDisplayed()
    }

    @Test
    fun `history screen displays different suitability scores with correct labels`() {
        val items = listOf(
            createSampleHistoryItem(
                activityName = "Excellent Activity",
                suitabilityLabel = "Excellent",
                suitabilityScore = 5,
                startDateTime = LocalDateTime.of(2025, 12, 6, 9, 0)
            ),
            createSampleHistoryItem(
                activityName = "Good Activity",
                suitabilityLabel = "Good",
                suitabilityScore = 3,
                startDateTime = LocalDateTime.of(2025, 12, 5, 9, 0)
            ),
            createSampleHistoryItem(
                activityName = "Poor Activity",
                suitabilityLabel = "Poor",
                suitabilityScore = 1,
                startDateTime = LocalDateTime.of(2025, 12, 4, 9, 0)
            )
        )
        renderWithItems(items)

        composeTestRule.onNodeWithText("Excellent")
            .assertIsDisplayed()

        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("Good"))
        composeTestRule.onNodeWithText("Good")
            .assertIsDisplayed()

        composeTestRule.onNode(hasScrollToNodeAction())
            .performScrollToNode(hasText("Poor"))
        composeTestRule.onNodeWithText("Poor")
            .assertIsDisplayed()
    }

    @Test
    fun `history screen hides empty state when items exist`() {
        val items = listOf(createSampleHistoryItem())
        renderWithItems(items)

        composeTestRule.onNodeWithText(context.getString(R.string.no_activity_history))
            .assertDoesNotExist()
    }
}

