package com.example.outdoorsy.di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module responsible for providing the DataStore instance.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    private const val USER_PREFERENCES_NAME = "settings"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    /**
     * Provides the singleton instance of Preferences DataStore.
     */
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> =
        appContext.dataStore
}
