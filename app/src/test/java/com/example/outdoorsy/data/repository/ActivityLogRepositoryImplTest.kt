package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.dao.ActivityLogDao
import com.example.outdoorsy.data.local.entity.ActivityEntity
import com.example.outdoorsy.data.local.entity.ActivityLogEntity
import com.example.outdoorsy.domain.model.ActivityLog
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityLogRepositoryImplTest {
    private lateinit var repository: ActivityLogRepositoryImpl
    private lateinit var activityLogDao: ActivityLogDao
    private lateinit var activityDao: ActivityDao

    @Before
    fun setup() {
        activityLogDao = mockk()
        activityDao = mockk()
        repository = ActivityLogRepositoryImpl(activityLogDao, activityDao)
    }

    @Test
    fun `getAllActivityLogs maps entities to domain with activity name`() = runTest {
        val start = LocalDateTime.of(2025, 12, 6, 10, 0)
        val end = start.plusHours(1)

        val logEntity = ActivityLogEntity(
            id = 1,
            location = "Park",
            activityId = 10,
            startDateTime = start,
            endDateTime = end,
            suitabilityLabel = "Good",
            suitabilityScore = 85
        )
        val activityEntity = ActivityEntity(id = 10, name = "Running")

        every { activityLogDao.getAll() } returns flowOf(listOf(logEntity))
        every { activityDao.getAll() } returns flowOf(listOf(activityEntity))

        val result = repository.getAllActivityLogs()
        val logs = result.first()

        assertEquals(1, logs.size)
        assertEquals("Running", logs.first().activityName)
        assertEquals("Park", logs.first().location)
        assertEquals(85, logs.first().suitabilityScore)
    }

    @Test
    fun `saveActivityLog inserts when activity exists`() = runTest {
        val activityEntity = ActivityEntity(id = 10, name = "Swimming")
        val log = ActivityLog(
            location = "Lake",
            activityName = "Swimming",
            startDateTime = LocalDateTime.of(2025, 12, 6, 9, 0),
            endDateTime = LocalDateTime.of(2025, 12, 6, 10, 0),
            suitabilityLabel = "Excellent",
            suitabilityScore = 95
        )

        every { activityDao.getByName("Swimming") } returns flowOf(activityEntity)
        coEvery { activityLogDao.insertActivityLog(any()) } just runs

        repository.saveActivityLog(log)

        coVerify {
            activityLogDao.insertActivityLog(match {
                it.activityId == 10 &&
                        it.location == "Lake" &&
                        it.suitabilityScore == 95
            })
        }
    }

    @Test
    fun `saveActivityLog does nothing when activity not found`() = runTest {
        val log = ActivityLog(
            location = "Gym",
            activityName = "Unknown",
            startDateTime = LocalDateTime.now(),
            endDateTime = LocalDateTime.now().plusHours(1),
            suitabilityLabel = "Poor",
            suitabilityScore = 20
        )

        every { activityDao.getByName("Unknown") } returns emptyFlow()

        repository.saveActivityLog(log)

        coVerify(exactly = 0) { activityLogDao.insertActivityLog(any()) }
    }

    @Test
    fun `deleteActivityLog removes when activity exists`() = runTest {
        val activityEntity = ActivityEntity(id = 10, name = "Running")
        val log = ActivityLog(
            location = "Park",
            activityName = "Running",
            startDateTime = LocalDateTime.now(),
            endDateTime = LocalDateTime.now().plusHours(1),
            suitabilityLabel = "Fair",
            suitabilityScore = 60
        )

        every { activityDao.getByName("Running") } returns flowOf(activityEntity)
        coEvery { activityLogDao.deleteActivityLog(any()) } just runs

        repository.deleteActivityLog(log)

        coVerify {
            activityLogDao.deleteActivityLog(match { it.activityId == 10 })
        }
    }

    @Test
    fun `deleteActivityLog does nothing when activity not found`() = runTest {
        val log = ActivityLog(
            location = "Gym",
            activityName = "Unknown",
            startDateTime = LocalDateTime.now(),
            endDateTime = LocalDateTime.now().plusHours(1),
            suitabilityLabel = "Bad",
            suitabilityScore = 10
        )

        every { activityDao.getByName("Unknown") } returns emptyFlow()

        repository.deleteActivityLog(log)

        coVerify(exactly = 0) { activityLogDao.deleteActivityLog(any()) }
    }
}
