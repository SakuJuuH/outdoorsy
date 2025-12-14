package com.example.outdoorsy.di.module

import android.content.Context
import androidx.room.Room
import com.example.outdoorsy.data.local.AppDatabase
import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.dao.ActivityLogDao
import com.example.outdoorsy.data.local.dao.CurrencyRateDao
import com.example.outdoorsy.data.local.dao.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides the Room Database instance and its Data Access Objects (DAOs).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the singleton instance of the AppDatabase.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase = Room.databaseBuilder(
        ctx,
        AppDatabase::class.java,
        "outdoorsy_db"
    )
        .fallbackToDestructiveMigration(true)
        .build()

    /** Provides the LocationDao. */
    @Provides
    fun provideLocationDao(database: AppDatabase): LocationDao = database.locationDao()

    /** Provides the ActivityDao. */
    @Provides
    fun provideActivityDao(database: AppDatabase): ActivityDao = database.activityDao()

    /** Provides the ActivityLogDao. */
    @Provides
    fun provideActivityLogDao(database: AppDatabase): ActivityLogDao = database.activityLogDao()

    /** Provides the CurrencyRateDao. */
    @Provides
    fun provideCurrencyRateDao(database: AppDatabase): CurrencyRateDao = database.currencyRateDao()
}
