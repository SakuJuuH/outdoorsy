package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.domain.model.weather.ForecastResponse

data class ForecastResponseDto(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItemDto>,
    val city: CityDto
)

fun ForecastResponseDto.toDomain(): ForecastResponse = ForecastResponse(
    count = cnt,
    listOfForecastItems = list.map { it.toDomain() },
    city = city.toDomain()
)
