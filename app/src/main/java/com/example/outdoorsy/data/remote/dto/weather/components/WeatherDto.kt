package com.example.outdoorsy.data.remote.dto.weather.components

import com.example.outdoorsy.domain.model.weather.components.Weather

data class WeatherDto(val id: Int, val main: String, val description: String, val icon: String) {
    fun toDomain(): Weather = Weather(
        id = id,
        group = main,
        description = description,
        icon = icon
    )
}
