package com.example.outdoorsy.di

import com.example.outdoorsy.data.remote.AiAssistantApiService
import com.example.outdoorsy.data.remote.ForecastApiService
import com.example.outdoorsy.data.remote.WeatherApiService
import com.example.outdoorsy.data.repository.AssistantRepositoryImpl
import com.example.outdoorsy.data.repository.ForecastRepositoryImpl
import com.example.outdoorsy.data.repository.WeatherRepositoryImpl
import com.example.outdoorsy.domain.repository.ForecastRepository
import com.example.outdoorsy.domain.repository.WeatherRepository
import com.example.outdoorsy.utils.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor())
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    @Provides
    @Singleton
    fun provideForecastApi(retrofit: Retrofit): ForecastApiService =
        retrofit.create(ForecastApiService::class.java)

    @Provides
    @Singleton
    fun provideAssistantApi(retrofit: Retrofit): AiAssistantApiService =
        retrofit.create(AiAssistantApiService::class.java)

    @Provides
    @Singleton
    fun provideWeatherRepository(weatherApiService: WeatherApiService): WeatherRepository =
        WeatherRepositoryImpl(weatherApiService)

    @Provides
    @Singleton
    fun provideForecastRepository(forecastApiService: ForecastApiService): ForecastRepository =
        ForecastRepositoryImpl(forecastApiService)
}
