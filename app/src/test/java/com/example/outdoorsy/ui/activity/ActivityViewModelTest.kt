package com.example.outdoorsy.ui.activity

import android.util.Log
import com.example.outdoorsy.domain.model.Activity
import com.example.outdoorsy.domain.model.Location
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import com.example.outdoorsy.domain.repository.ActivityRepository
import com.example.outdoorsy.domain.repository.LocationRepository
import com.example.outdoorsy.domain.repository.SettingsRepository
import com.example.outdoorsy.domain.usecase.GetAiAssistant
import com.example.outdoorsy.domain.usecase.GetForecast
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityViewModelTest {
    private lateinit var viewModel: ActivityViewModel

    @MockK
    private lateinit var getAiAssistant: GetAiAssistant

    @MockK
    private lateinit var getForecast: GetForecast

    @MockK
    private lateinit var settingsRepository: SettingsRepository

    @MockK
    private lateinit var activityLogRepository: ActivityLogRepository

    @MockK
    private lateinit var activityRepository: ActivityRepository

    @MockK
    private lateinit var locationRepository: LocationRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // Mock static Log to avoid Android dependency issues
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        // Default flows
        every { activityRepository.getAllActivities() } returns flowOf(listOf(Activity("Running")))
        every { locationRepository.getAllLocations() } returns flowOf(
            listOf(
                Location(
                    "Park",
                    latitude = 60.0,
                    longitude = 25.0
                )
            )
        )

        viewModel = ActivityViewModel(
            getAiAssistant,
            getForecast,
            settingsRepository,
            activityLogRepository,
            activityRepository,
            locationRepository
        )
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads activities and locations`() = runTest {
        val state = viewModel.uiState.value
        assertEquals(1, state.activities.size)
        assertEquals("Running", state.activities.first().name)
        assertEquals("Park", state.locations.first().name)
    }

    @Test
    fun `updateLocation sets selectedLocation`() = runTest {
        viewModel.updateLocation("Park")
        assertEquals("Park", viewModel.uiState.value.selectedLocation?.name)
    }

    @Test
    fun `updateActivity sets selectedActivity`() = runTest {
        viewModel.updateActivity("Running")
        assertEquals("Running", viewModel.uiState.value.selectedActivity?.name)
    }

    @Test
    fun `updateStartDateTime adjusts end time if invalid`() = runTest {
        val startDate = LocalDate.now()
        val startTime = LocalTime.of(10, 0)
        val endTime = LocalTime.of(9, 0)

        viewModel.updateStartDateTime(startDate, startTime, startDate, endTime)

        val state = viewModel.uiState.value
        assertEquals(com.example.outdoorsy.R.string.time_error_adjusted, state.timeRangeErrorId)
        assertEquals(LocalTime.of(11, 0), state.selectedEndTime)
    }

    @Test
    fun `addActivity saves new activity if not exists`() = runTest {
        coEvery { activityRepository.saveActivity(any()) } just runs
        viewModel.addActivity("Swimming")
        coVerify { activityRepository.saveActivity(Activity("Swimming")) }
    }

    @Test
    fun `deleteActivity removes activity and clears selection`() = runTest {
        val activity = Activity("Running")
        every { activityRepository.getAllActivities() } returns flowOf(listOf(activity))
        viewModel.updateActivity("Running")

        coEvery { activityRepository.deleteActivityByName(activity) } just runs
        viewModel.deleteActivity("Running")

        coVerify { activityRepository.deleteActivityByName(activity) }
        assertNull(viewModel.uiState.value.selectedActivity)
    }
}
