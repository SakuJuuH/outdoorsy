package com.example.outdoorsy

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.outdoorsy.data.local.AppDatabase
import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.dao.LocationDao
import com.example.outdoorsy.data.local.entity.ActivityEntity
import com.example.outdoorsy.data.local.entity.LocationEntity
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var activityDao: ActivityDao
    private lateinit var locationDao: LocationDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(context = context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        activityDao = db.activityDao()
        locationDao = db.locationDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndReadLocation() = runTest {
        val location = LocationEntity(id = 1, name = "Helsinki", latitude = 60.1699, longitude = 24.9384)

        locationDao.insertLocation(location)

        val allLocations = locationDao.getAll().first()
        Log.d("TEST", "insertAndReadLocation: $allLocations")
        assertThat(allLocations).isNotEmpty()
        assertThat(allLocations).hasSize(1)
        assertThat(allLocations[0].name).isEqualTo("Helsinki")
        assertThat(allLocations[0].latitude).isEqualTo(60.1699)
    }

    @Test
    @Throws(Exception::class)
    fun insertAndReadActivity() = runTest {
        val activity = ActivityEntity(id = 1, name = "Hiking")

        activityDao.insertActivity(activity)

        val retrievedActivity = activityDao.getByName("Hiking").first()
        Log.d("TEST", "insertAndReadActivity: $retrievedActivity")
        assertThat(retrievedActivity).isNotNull()
        assertThat(retrievedActivity.name).isEqualTo("Hiking")
        assertThat(retrievedActivity.id).isEqualTo(1)
    }

    @Test
    @Throws(Exception::class)
    fun checkAutoIncrementActivityId() = runTest {
        val activity1 = ActivityEntity(name = "Cycling")
        val activity2 = ActivityEntity(name = "Kayaking")

        activityDao.insertActivity(activity1)
        activityDao.insertActivity(activity2)

        val allActivities = activityDao.getAll().first()
        assertThat(allActivities).hasSize(2)
        assertThat(allActivities[0].id).isEqualTo(1)
        assertThat(allActivities[1].id).isEqualTo(2)
        assertThat(allActivities[0].name).isEqualTo("Cycling")
        assertThat(allActivities[1].name).isEqualTo("Kayaking")
    }

    @Test
    @Throws(Exception::class)
    fun deleteActivity() = runTest {
        val activity1 = ActivityEntity(name = "Hiking")
        val activity2 = ActivityEntity(name = "Cycling")

        activityDao.insertActivity(activity1)
        activityDao.insertActivity(activity2)

        val activityToDelete = activityDao.getByName("Hiking").first()
        Log.d("TEST", "deleteActivity: $activityToDelete")

        activityDao.deleteActivity(activityToDelete)

        val remainingActivities = activityDao.getAll().first()
        Log.d("TEST", "deleteActivity: $remainingActivities")
        assertThat(remainingActivities).hasSize(1)
        assertThat(remainingActivities[0].name).isEqualTo("Cycling")
    }
}
