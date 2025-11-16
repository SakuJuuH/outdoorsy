package com.example.outdoorsy.di

import com.example.outdoorsy.data.repository.EbayRepositoryImpl
import com.example.outdoorsy.data.repository.ForecastRepositoryImpl
import com.example.outdoorsy.data.repository.WeatherRepositoryImpl
import com.example.outdoorsy.domain.repository.EbayRepository
import com.example.outdoorsy.domain.repository.ForecastRepository
import com.example.outdoorsy.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}
