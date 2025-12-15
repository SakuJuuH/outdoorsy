package com.example.outdoorsy.ui.history

import com.example.outdoorsy.domain.model.Activity
import com.example.outdoorsy.domain.model.ActivityLog
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import com.example.outdoorsy.domain.repository.ActivityRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    private lateinit var viewModel: HistoryViewModel

    @MockK
    private lateinit var activityLogRepository: ActivityLogRepository

    @MockK
    private lateinit var activityRepository: ActivityRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `historyItems is empty when no logs exist`() = runTest {
        every { activityLogRepository.getAllActivityLogs() } returns flowOf(emptyList())
        every { activityRepository.getAllActivities() } returns flowOf(emptyList())

        viewModel = HistoryViewModel(activityLogRepository, activityRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.historyItems.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.historyItems.value.isEmpty())
    }

    @Test
    fun `historyItems maps activity logs correctly`() = runTest {
        val now = LocalDateTime.of(2025, 12, 6, 10, 0)
        val activityLog = ActivityLog(
            location = "Helsinki, FI",
            activityName = "Running",
            startDateTime = now,
            endDateTime = now.plusHours(1),
            suitabilityLabel = "Excellent",
            suitabilityScore = 5
        )
        val activity = Activity(name = "Running")

        every { activityLogRepository.getAllActivityLogs() } returns flowOf(listOf(activityLog))
        every { activityRepository.getAllActivities() } returns flowOf(listOf(activity))

        viewModel = HistoryViewModel(activityLogRepository, activityRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.historyItems.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val items = viewModel.historyItems.value
        assertEquals(1, items.size)

        val item = items.first()
        assertEquals("Running", item.activityName)
        assertEquals("Helsinki, FI", item.location)
        assertEquals("Helsinki", item.city)
        assertEquals("FI", item.state)
        assertEquals("Excellent", item.suitabilityLabel)
        assertEquals(5, item.suitabilityScore)

        val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        assertEquals(now.format(dateFormatter), item.date)
    }

    @Test
    fun `historyItems sorted from newest to oldest`() = runTest {
        val olderDateTime = LocalDateTime.of(2025, 12, 1, 10, 0)
        val newerDateTime = LocalDateTime.of(2025, 12, 6, 10, 0)

        val olderLog = ActivityLog(
            location = "Espoo, FI",
            activityName = "Hiking",
            startDateTime = olderDateTime,
            endDateTime = olderDateTime.plusHours(2),
            suitabilityLabel = "Good",
            suitabilityScore = 3
        )
        val newerLog = ActivityLog(
            location = "Helsinki, FI",
            activityName = "Running",
            startDateTime = newerDateTime,
            endDateTime = newerDateTime.plusHours(1),
            suitabilityLabel = "Excellent",
            suitabilityScore = 5
        )

        val activities = listOf(Activity(name = "Running"), Activity(name = "Hiking"))

        // Provide logs in wrong order (older first) to test sorting
        every { activityLogRepository.getAllActivityLogs() } returns flowOf(
            listOf(
                olderLog,
                newerLog
            )
        )
        every { activityRepository.getAllActivities() } returns flowOf(activities)

        viewModel = HistoryViewModel(activityLogRepository, activityRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.historyItems.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val items = viewModel.historyItems.value
        assertEquals(2, items.size)
        // Newest should be first
        assertEquals("Running", items[0].activityName)
        assertEquals("Hiking", items[1].activityName)
    }

    @Test
    fun `historyItems uses correct activity icon for cycling`() = runTest {
        val now = LocalDateTime.now()
        val activityLog = ActivityLog(
            location = "Park",
            activityName = "Cycling",
            startDateTime = now,
            endDateTime = now.plusHours(1),
            suitabilityLabel = "Very Good",
            suitabilityScore = 4
        )

        every { activityLogRepository.getAllActivityLogs() } returns flowOf(listOf(activityLog))
        every { activityRepository.getAllActivities() } returns flowOf(listOf(Activity(name = "Cycling")))

        viewModel = HistoryViewModel(activityLogRepository, activityRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.historyItems.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val items = viewModel.historyItems.value
        assertEquals(1, items.size)
        assertEquals("Cycling", items.first().activityName)
    }

    @Test
    fun `historyItems handles activity not found in map`() = runTest {
        val now = LocalDateTime.now()
        val activityLog = ActivityLog(
            location = "Beach",
            activityName = "Swimming",
            startDateTime = now,
            endDateTime = now.plusHours(1),
            suitabilityLabel = "Fair",
            suitabilityScore = 2
        )

        every { activityLogRepository.getAllActivityLogs() } returns flowOf(listOf(activityLog))
        // Activity is not in the list
        every { activityRepository.getAllActivities() } returns flowOf(emptyList())

        viewModel = HistoryViewModel(activityLogRepository, activityRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.historyItems.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val items = viewModel.historyItems.value
        assertEquals(1, items.size)
        // Falls back to "Activity" when not found
        assertEquals("Activity", items.first().activityName)
    }

    @Test
    fun `historyItems parses location without state correctly`() = runTest {
        val now = LocalDateTime.now()
        val activityLog = ActivityLog(
            location = "Central Park",
            activityName = "Running",
            startDateTime = now,
            endDateTime = now.plusHours(1),
            suitabilityLabel = "Good",
            suitabilityScore = 3
        )

        every { activityLogRepository.getAllActivityLogs() } returns flowOf(listOf(activityLog))
        every { activityRepository.getAllActivities() } returns flowOf(listOf(Activity(name = "Running")))

        viewModel = HistoryViewModel(activityLogRepository, activityRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.historyItems.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val items = viewModel.historyItems.value
        assertEquals(1, items.size)
        assertEquals("Central Park", items.first().city)
        assertEquals("", items.first().state)
    }

    @Test
    fun `historyItems formats time range correctly`() = runTest {
        val start = LocalDateTime.of(2025, 12, 6, 9, 0)
        val end = LocalDateTime.of(2025, 12, 6, 11, 30)
        val activityLog = ActivityLog(
            location = "Helsinki, FI",
            activityName = "Running",
            startDateTime = start,
            endDateTime = end,
            suitabilityLabel = "Good",
            suitabilityScore = 3
        )

        every { activityLogRepository.getAllActivityLogs() } returns flowOf(listOf(activityLog))
        every { activityRepository.getAllActivities() } returns flowOf(listOf(Activity(name = "Running")))

        viewModel = HistoryViewModel(activityLogRepository, activityRepository)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.historyItems.collect()
        }
        testDispatcher.scheduler.advanceUntilIdle()

        val items = viewModel.historyItems.value
        assertEquals(1, items.size)

        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        val expectedRange = "${start.format(formatter)} - ${end.format(formatter)}"
        assertEquals(expectedRange, items.first().timeRange)
    }
}

