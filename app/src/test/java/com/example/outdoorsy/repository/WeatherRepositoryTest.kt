package com.example.outdoorsy.repository

import com.example.outdoorsy.data.remote.WeatherApiService
import com.example.outdoorsy.data.repository.WeatherRepositoryImpl
import com.example.outdoorsy.di.interceptor.OpenWeatherInterceptor
import com.example.outdoorsy.utils.OWM_BASE_URL
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepositoryTest {
    private lateinit var gson: Gson
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(OpenWeatherInterceptor())
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(OWM_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherApiService::class.java)
        repository = WeatherRepositoryImpl(weatherService)

        gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
    }

    @Test
    fun `test getCurrentWeatherByCity`() = runTest {
        val result = repository.getCurrentWeatherByCity("Helsinki", "metric", "fi")
        println("Success: ${gson.toJson(result)}")
        assertTrue("City name should not be empty", result.name.isNotEmpty())
    }

    @Test
    fun `test getCurrentWeatherByCoordinates`() = runTest {
        val result = repository.getCurrentWeatherByCoordinates(
            60.192059,
            24.94583,
            "metric",
            "fi"
        )
        println("Success: ${gson.toJson(result)}")
        assertTrue("City name should not be empty", result.name.isNotEmpty())
    }
}
