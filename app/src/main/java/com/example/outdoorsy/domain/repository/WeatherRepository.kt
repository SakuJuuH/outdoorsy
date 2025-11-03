package com.example.outdoorsy.domain.repository

import com.example.outdoorsy.domain.model.WeatherResponse

interface WeatherRepository {
    suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): WeatherResponse

    suspend fun getCurrentWeatherByCity(city: String, units: String, lang: String): WeatherResponse
}
