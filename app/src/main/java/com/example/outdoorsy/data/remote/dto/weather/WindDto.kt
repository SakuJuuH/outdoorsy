package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.domain.model.weather.Wind

data class WindDto(val speed: Double, val deg: Int, val gust: Double)

fun WindDto.toDomain(): Wind = Wind(
    speed = speed,
    degrees = deg,
    gust = gust
)
