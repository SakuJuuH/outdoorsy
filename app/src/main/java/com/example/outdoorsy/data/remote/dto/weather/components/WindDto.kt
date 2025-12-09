package com.example.outdoorsy.data.remote.dto.weather.components

import com.example.outdoorsy.domain.model.weather.components.Wind

data class WindDto(val speed: Double, val deg: Int, val gust: Double) {
    fun toDomain(): Wind = Wind(
        speed = speed,
        degrees = deg,
        gust = gust
    )
}
