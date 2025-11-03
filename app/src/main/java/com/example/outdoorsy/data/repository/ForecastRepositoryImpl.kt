package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.remote.ForecastApiService
import com.example.outdoorsy.data.remote.dto.toDomain
import com.example.outdoorsy.domain.model.ForecastResponse
import com.example.outdoorsy.domain.repository.ForecastRepository
import javax.inject.Inject

class ForecastRepositoryImpl @Inject constructor(private val forecastService: ForecastApiService) :
    ForecastRepository {
    override suspend fun getForecastByCoordinates(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): ForecastResponse {
        val dto = forecastService.getForecastByCoordinates(latitude, longitude, units, lang)
        return dto.toDomain()
    }

    override suspend fun getForecastByCity(
        city: String,
        units: String,
        lang: String
    ): ForecastResponse {
        val dto = forecastService.getForecastByCity(city = city, units = units, language = lang)
        return dto.toDomain()
    }
}
