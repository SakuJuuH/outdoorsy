package com.example.outdoorsy.di.module

import android.content.Context
import com.example.outdoorsy.data.repository.ActivityLogRepositoryImpl
import com.example.outdoorsy.data.repository.ActivityRepositoryImpl
import com.example.outdoorsy.data.repository.CurrencyRepositoryImpl
import com.example.outdoorsy.data.repository.EbayRepositoryImpl
import com.example.outdoorsy.data.repository.ForecastRepositoryImpl
import com.example.outdoorsy.data.repository.LocationRepositoryImpl
import com.example.outdoorsy.data.repository.SettingsRepositoryImpl
import com.example.outdoorsy.data.repository.WeatherRepositoryImpl
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import com.example.outdoorsy.domain.repository.ActivityRepository
import com.example.outdoorsy.domain.repository.CurrencyRepository
import com.example.outdoorsy.domain.repository.EbayRepository
import com.example.outdoorsy.domain.repository.ForecastRepository
import com.example.outdoorsy.domain.repository.LocationRepository
import com.example.outdoorsy.domain.repository.SettingsRepository
import com.example.outdoorsy.domain.repository.WeatherRepository
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
 * Hilt module that binds Repository interfaces to their concrete implementations.
 * This allows injecting the interface while Hilt provides the implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindForecastRepository(impl: ForecastRepositoryImpl): ForecastRepository

    @Binds
    @Singleton
    abstract fun bindEbayRepository(impl: EbayRepositoryImpl): EbayRepository

    @Binds
    @Singleton
    abstract fun bindActivityLogRepository(impl: ActivityLogRepositoryImpl): ActivityLogRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(impl: ActivityRepositoryImpl): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(impl: CurrencyRepositoryImpl): CurrencyRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    companion object {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext ctx: Context
        ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)
    }
}
