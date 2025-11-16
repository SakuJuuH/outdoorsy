package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.domain.model.weather.Clouds

data class CloudsDto(val all: Int)

fun CloudsDto.toDomain(): Clouds = Clouds(
    cloudiness = all
)
