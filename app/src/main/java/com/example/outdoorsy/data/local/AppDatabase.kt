package com.example.outdoorsy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.dao.ActivityLogDao
import com.example.outdoorsy.data.local.dao.CurrencyRateDao
import com.example.outdoorsy.data.local.dao.LocationDao
import com.example.outdoorsy.data.local.entity.ActivityEntity
import com.example.outdoorsy.data.local.entity.ActivityLogEntity
import com.example.outdoorsy.data.local.entity.CurrencyRateEntity
import com.example.outdoorsy.data.local.entity.LocationEntity
import com.example.outdoorsy.utils.DateTimeConverters

@Database(
    entities = [
        ActivityEntity::class,
        LocationEntity::class,
        ActivityLogEntity::class,
        CurrencyRateEntity::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun locationDao(): LocationDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun currencyRateDao(): CurrencyRateDao
}
