package com.example.outdoorsy.domain.usecase

import com.example.outdoorsy.domain.model.weather.WeatherResponse
import com.example.outdoorsy.domain.repository.WeatherRepository
import javax.inject.Inject

class GetCurrentWeather @Inject constructor(private val repository: WeatherRepository) {
    suspend operator fun invoke(
        lat: Double? = null,
        lon: Double? = null,
        city: String? = null,
        units: String,
        language: String
    ): WeatherResponse = if (city != null) {
        repository.getCurrentWeatherByCity(city, units, language)
    } else if (lat != null && lon != null) {
        repository.getCurrentWeatherByCoordinates(lat, lon, units, language)
    } else {
        throw IllegalArgumentException("No coordinates or city provided")
    }
}
