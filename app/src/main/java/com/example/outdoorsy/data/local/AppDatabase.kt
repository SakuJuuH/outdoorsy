package com.example.outdoorsy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.dao.LocationDao
import com.example.outdoorsy.data.local.entity.Activity
import com.example.outdoorsy.data.local.entity.Location

@Database(
    entities = [Activity::class, Location::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun locationDao(): LocationDao
}
