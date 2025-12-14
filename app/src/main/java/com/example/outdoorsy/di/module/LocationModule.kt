package com.example.outdoorsy.di.module

import android.content.Context
import com.example.outdoorsy.data.repository.LocationRepositoryImpl
import com.example.outdoorsy.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Location-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    /** Binds the implementation [LocationRepositoryImpl] to the interface [LocationRepository]. */
    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    companion object {
        /**
         * Provides the FusedLocationProviderClient from Google Play Services.
         */
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext ctx: Context
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)
    }
}
