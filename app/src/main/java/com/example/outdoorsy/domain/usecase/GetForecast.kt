package com.example.outdoorsy.domain.usecase

import com.example.outdoorsy.domain.model.weather.ForecastResponse
import com.example.outdoorsy.domain.repository.ForecastRepository
import javax.inject.Inject

class GetForecast @Inject constructor(private val repository: ForecastRepository) {
    suspend operator fun invoke(
        lat: Double? = null,
        lon: Double? = null,
        city: String? = null,
        units: String,
        language: String
    ): ForecastResponse = if (lat != null && lon != null) {
        repository.getForecastByCoordinates(lat, lon, units, language)
    } else if (city != null) {
        repository.getForecastByCity(city, units, language)
    } else {
        throw IllegalArgumentException("No coordinates or city provided")
    }
}
