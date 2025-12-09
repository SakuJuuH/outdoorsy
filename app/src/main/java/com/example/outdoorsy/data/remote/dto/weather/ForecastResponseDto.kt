package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.data.remote.dto.weather.components.CityDto
import com.example.outdoorsy.data.remote.dto.weather.components.ForecastItemDto
import com.example.outdoorsy.domain.model.weather.ForecastResponse

data class ForecastResponseDto(val list: List<ForecastItemDto>, val city: CityDto) {
    fun toDomain(): ForecastResponse = ForecastResponse(
        listOfForecastItems = list.map { it.toDomain() },
        city = city.toDomain()
    )
}
