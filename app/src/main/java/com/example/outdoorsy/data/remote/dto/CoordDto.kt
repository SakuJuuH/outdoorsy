package com.example.outdoorsy.data.remote.dto

import com.example.outdoorsy.domain.model.Coord

data class CoordDto(val lon: Double, val lat: Double)

fun CoordDto.toDomain(): Coord = Coord(
    longitude = lon,
    latitude = lat
)
