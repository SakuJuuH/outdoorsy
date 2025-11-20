package com.example.outdoorsy.domain.repository

import com.example.outdoorsy.domain.model.weather.ForecastResponse

interface ForecastRepository {
    suspend fun getForecastByCoordinates(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): ForecastResponse

    suspend fun getForecastByCity(city: String, units: String, lang: String): ForecastResponse
}
