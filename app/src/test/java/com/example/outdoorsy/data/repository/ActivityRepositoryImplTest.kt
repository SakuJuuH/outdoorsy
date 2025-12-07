package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.entity.ActivityEntity
import com.example.outdoorsy.domain.model.Activity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivityRepositoryImplTest {

    private lateinit var repository: ActivityRepositoryImpl
    private lateinit var activityDao: ActivityDao

    @Before
    fun setup() {
        activityDao = mockk()
        repository = ActivityRepositoryImpl(activityDao)
    }

    @Test
    fun `getClothingItems returns stored items`() {
        repository.setClothingItems(listOf("Jacket", "Boots"))
        val items = repository.getClothingItems()
        assertEquals(listOf("Jacket", "Boots"), items)
    }

    @Test
    fun `getAllActivities maps entities to domain`() = runTest {
        val entities = listOf(ActivityEntity(id = 1, name = "Running"))
        every { activityDao.getAll() } returns flowOf(entities)

        val result = repository.getAllActivities()
        val activities = result.first()

        assertEquals(1, activities.size)
        assertEquals("Running", activities.first().name)
    }

    @Test
    fun `getActivityById maps entity to domain`() = runTest {
        val entity = ActivityEntity(id = 2, name = "Swimming")
        every { activityDao.getById(2) } returns flowOf(entity)

        val result = repository.getActivityById(2)
        val activity = result.first()

        assertEquals("Swimming", activity.name)
    }

    @Test
    fun `saveActivity calls insertActivity with entity`() {
        val activity = Activity("Hiking")
        val entity = activity.toEntity()

        every { activityDao.insertActivity(entity) } just runs

        repository.saveActivity(activity)

        verify { activityDao.insertActivity(entity) }
    }

    @Test
    fun `deleteActivityByName calls DAO with name`() {
        val activity = Activity("Gardening")

        every { activityDao.deleteActivityByName("Gardening") } just runs

        repository.deleteActivityByName(activity)

        verify { activityDao.deleteActivityByName("Gardening") }
    }
}
