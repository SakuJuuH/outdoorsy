package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.domain.model.weather.Weather

data class WeatherDto(val id: Int, val main: String, val description: String, val icon: String)

fun WeatherDto.toDomain(): Weather = Weather(
    id = id,
    group = main,
    description = description,
    icon = icon
)
