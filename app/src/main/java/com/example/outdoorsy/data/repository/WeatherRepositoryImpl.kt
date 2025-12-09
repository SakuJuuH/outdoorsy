package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.remote.WeatherApiService
import com.example.outdoorsy.domain.model.weather.WeatherResponse
import com.example.outdoorsy.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(private val weatherApi: WeatherApiService) :
    WeatherRepository {
    override suspend fun getCurrentWeatherByCity(
        city: String,
        units: String,
        lang: String
    ): WeatherResponse {
        val dto =
            weatherApi.getCurrentWeatherByCity(city = city, units = units, language = lang)
        return dto.toDomain()
    }

    override suspend fun getCurrentWeatherByCoordinates(
        latitude: Double,
        longitude: Double,
        units: String,
        lang: String
    ): WeatherResponse {
        val dto = weatherApi.getCurrentWeatherByCoordinates(
            latitude = latitude,
            longitude = longitude,
            units = units,
            language = lang
        )
        return dto.toDomain()
    }
}
