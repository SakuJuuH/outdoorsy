package com.example.outdoorsy.di.module

import com.example.outdoorsy.data.remote.AiAssistantApiService
import com.example.outdoorsy.data.remote.CurrencyApiService
import com.example.outdoorsy.data.remote.EbayApiService
import com.example.outdoorsy.data.remote.EbayAuthService
import com.example.outdoorsy.data.remote.ForecastApiService
import com.example.outdoorsy.data.remote.WeatherApiService
import com.example.outdoorsy.data.repository.AssistantRepositoryImpl
import com.example.outdoorsy.di.CurrencyApi
import com.example.outdoorsy.di.EbayApi
import com.example.outdoorsy.di.EbayAuth
import com.example.outdoorsy.di.OpenWeather
import com.example.outdoorsy.di.interceptor.CurrencyApiAuthInterceptor
import com.example.outdoorsy.di.interceptor.EbayAccessAuthInterceptor
import com.example.outdoorsy.di.interceptor.EbayAuthInterceptor
import com.example.outdoorsy.di.interceptor.OpenWeatherInterceptor
import com.example.outdoorsy.domain.repository.AssistantRepository
import com.example.outdoorsy.utils.CURRENCY_BASE_URL
import com.example.outdoorsy.utils.EBAY_BASE_URL
import com.example.outdoorsy.utils.OWM_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Hilt module responsible for providing all Network-related dependencies,
 * including OkHttpClient, Retrofit instances, and API Service interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** Provides a logging interceptor for debugging network requests. */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Provides the OkHttpClient specifically for OpenWeatherMap requests,
     * including the [OpenWeatherInterceptor].
     */
    @Provides
    @Singleton
    @OpenWeather
    fun provideOpenWeatherOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        openWeatherInterceptor: OpenWeatherInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(openWeatherInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Provides the OkHttpClient for standard eBay API requests,
     * including the [EbayAuthInterceptor] for Bearer tokens.
     */
    @Provides
    @Singleton
    @EbayApi
    fun provideEbayApiOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        ebayAuthInterceptor: EbayAuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(ebayAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Provides the OkHttpClient for eBay Authentication requests (fetching tokens),
     * including the [EbayAccessAuthInterceptor] for Basic Auth.
     */
    @Provides
    @Singleton
    @EbayAuth
    fun provideEbayAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        ebayAccessAuthInterceptor: EbayAccessAuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(ebayAccessAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Provides the OkHttpClient for Currency API requests,
     * including the [CurrencyApiAuthInterceptor].
     */
    @Provides
    @Singleton
    @CurrencyApi
    fun provideCurrencyApiOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        currencyApiAuthInterceptor: CurrencyApiAuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(currencyApiAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /** Provides the Retrofit instance for OpenWeatherMap. */
    @Provides
    @Singleton
    @OpenWeather
    fun provideOpenWeatherRetrofit(@OpenWeather client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(OWM_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Provides the Retrofit instance for eBay API. */
    @Provides
    @Singleton
    @EbayApi
    fun provideEbayApiRetrofit(@EbayApi client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(EBAY_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Provides the Retrofit instance for eBay Auth. */
    @Provides
    @Singleton
    @EbayAuth
    fun provideEbayAuthRetrofit(@EbayAuth client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(EBAY_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Provides the Retrofit instance for Currency API. */
    @Provides
    @Singleton
    @CurrencyApi
    fun provideCurrencyApiRetrofit(@CurrencyApi client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(CURRENCY_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /** Provides the WeatherApiService. */
    @Provides
    @Singleton
    fun provideWeatherApi(@OpenWeather retrofit: Retrofit): WeatherApiService =
        retrofit.create(WeatherApiService::class.java)

    /** Provides the ForecastApiService. */
    @Provides
    @Singleton
    fun provideForecastApi(@OpenWeather retrofit: Retrofit): ForecastApiService =
        retrofit.create(ForecastApiService::class.java)

    /** Provides the AiAssistantApiService. */
    @Provides
    @Singleton
    fun provideAssistantApi(@OpenWeather retrofit: Retrofit): AiAssistantApiService =
        retrofit.create(AiAssistantApiService::class.java)

    /** Provides the EbayApiService. */
    @Provides
    @Singleton
    @EbayApi
    fun provideEbayApi(@EbayApi retrofit: Retrofit): EbayApiService =
        retrofit.create(EbayApiService::class.java)

    /** Provides the EbayAuthService. */
    @Provides
    @Singleton
    @EbayAuth
    fun provideEbayAuthApi(@EbayAuth retrofit: Retrofit): EbayAuthService =
        retrofit.create(EbayAuthService::class.java)

    /** Provides the CurrencyApiService. */
    @Provides
    @Singleton
    fun provideCurrencyApiService(@CurrencyApi retrofit: Retrofit): CurrencyApiService =
        retrofit.create(CurrencyApiService::class.java)

    /** Provides the AssistantRepository manually (if not bound via Binds). */
    @Provides
    @Singleton
    fun provideAssistantRepository(
        aiAssistantApiService: AiAssistantApiService
    ): AssistantRepository = AssistantRepositoryImpl(aiAssistantApiService)
}
