package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.remote.WeatherApiService
import com.example.outdoorsy.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApiService,
) : WeatherRepository {
}