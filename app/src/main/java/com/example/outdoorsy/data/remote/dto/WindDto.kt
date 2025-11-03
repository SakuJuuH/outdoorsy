package com.example.outdoorsy.data.remote.dto

import com.example.outdoorsy.domain.model.Wind

data class WindDto(val speed: Double, val deg: Int, val gust: Double)

fun WindDto.toDomain(): Wind = Wind(
    speed = speed,
    degrees = deg,
    gust = gust
)
