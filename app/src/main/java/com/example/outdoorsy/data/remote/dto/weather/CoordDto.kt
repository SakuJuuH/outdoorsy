package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.domain.model.weather.Coord

data class CoordDto(val lon: Double, val lat: Double)

fun CoordDto.toDomain(): Coord = Coord(
    longitude = lon,
    latitude = lat
)
