package com.example.outdoorsy.di

import com.example.outdoorsy.data.remote.CurrencyApiService
import com.example.outdoorsy.data.repository.CurrencyRepositoryImpl
import com.example.outdoorsy.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CurrencyApiModule {

    @Provides
    @Singleton
    @CurrencyApi
    fun provideCurrencyApiRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.currencyapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideCurrencyApiService(@CurrencyApi retrofit: Retrofit): CurrencyApiService =
        retrofit.create(CurrencyApiService::class.java)

    @Provides
    @Singleton
    fun provideCurrencyRepository(currencyRepositoryImpl: CurrencyRepositoryImpl): CurrencyRepository =
        currencyRepositoryImpl
}
