package com.example.outdoorsy.di

import com.example.outdoorsy.data.remote.AiAssistantApiService
import com.example.outdoorsy.data.remote.EbayApiService
import com.example.outdoorsy.data.remote.EbayAuthService
import com.example.outdoorsy.data.remote.ForecastApiService
import com.example.outdoorsy.data.remote.WeatherApiService
import com.example.outdoorsy.di.interceptor.EbayAccessAuthInterceptor
import com.example.outdoorsy.di.interceptor.EbayAuthInterceptor
import com.example.outdoorsy.di.interceptor.OpenWeatherInterceptor
import com.example.outdoorsy.utils.EBAY_BASE_URL
import com.example.outdoorsy.utils.OWM_BASE_URL
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
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    @OpenWeather
    fun provideOpenWeatherOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        openWeatherInterceptor: OpenWeatherInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(openWeatherInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    @EbayApi
    fun provideEbayApiOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        ebayAuthInterceptor: EbayAuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ebayAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    @EbayAuth
    fun provideEbayAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        ebayAccessAuthInterceptor: EbayAccessAuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ebayAccessAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    @OpenWeather
    fun provideOpenWeatherRetrofit(@OpenWeather client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(OWM_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @EbayApi
    fun provideEbayApiRetrofit(@EbayApi client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(EBAY_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @EbayAuth
    fun provideEbayAuthRetrofit(@EbayAuth client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(EBAY_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideWeatherApi(@OpenWeather retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    @Provides
    @Singleton
    fun provideForecastApi(@OpenWeather retrofit: Retrofit): ForecastApiService =
        retrofit.create(ForecastApiService::class.java)

    @Provides
    @Singleton
    fun provideAssistantApi(@OpenWeather retrofit: Retrofit): AiAssistantApiService =
        retrofit.create(AiAssistantApiService::class.java)

    @Provides
    @Singleton
    @EbayApi
    fun provideEbayApi(@EbayApi retrofit: Retrofit): EbayApiService =
        retrofit.create(EbayApiService::class.java)

    @Provides
    @Singleton
    @EbayAuth
    fun provideEbayAuthApi(@EbayAuth retrofit: Retrofit): EbayAuthService =
        retrofit.create(EbayAuthService::class.java)
}
