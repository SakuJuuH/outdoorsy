package com.example.outdoorsy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.dao.ActivityLogDao
import com.example.outdoorsy.data.local.dao.LocationDao
import com.example.outdoorsy.data.local.entity.Activity
import com.example.outdoorsy.data.local.entity.ActivityLog
import com.example.outdoorsy.data.local.entity.Location
import com.example.outdoorsy.utils.DateTimeConverters

@Database(
    entities = [Activity::class, Location::class, ActivityLog::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun locationDao(): LocationDao
    abstract fun activityLogDao(): ActivityLogDao
}
