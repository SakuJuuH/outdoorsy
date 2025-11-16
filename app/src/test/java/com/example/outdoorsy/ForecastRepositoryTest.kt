package com.example.outdoorsy

import com.example.outdoorsy.data.remote.ForecastApiService
import com.example.outdoorsy.data.repository.ForecastRepositoryImpl
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

class ForecastRepositoryTest {
    private lateinit var gson: Gson
    private lateinit var repository: ForecastRepositoryImpl

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

        val forecastService = retrofit.create(ForecastApiService::class.java)
        repository = ForecastRepositoryImpl(forecastService)

        gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
    }

    @Test
    fun `test getForecastByCity`() = runTest {
        val result = repository.getForecastByCity(
            "Helsinki",
            "metric",
            "fi"
        )

        println("Success: ${gson.toJson(result)}")
        assertTrue("Count should be greater than 0", result.count > 0)
        assertTrue("List should not be empty", result.listOfForecastItems.isNotEmpty())
    }

    @Test
    fun `test getForecastByCoordinates`() = runTest {
        val result = repository.getForecastByCoordinates(
            60.192059,
            24.94583,
            "metric",
            "fi"
        )

        println("Success: ${gson.toJson(result)}")
        assertTrue("Count should be greater than 0", result.count > 0)
        assertTrue("List should not be empty", result.listOfForecastItems.isNotEmpty())
    }
}
