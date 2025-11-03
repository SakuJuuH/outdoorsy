package com.example.outdoorsy.data.remote

import com.example.outdoorsy.data.remote.dto.ForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApiService {
    @GET("data/2.5/forecast")
    suspend fun getForecastByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en"
    ): ForecastResponseDto

    @GET("data/2.5/forecast")
    suspend fun getForecastByCity(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en"
    ): ForecastResponseDto
}
