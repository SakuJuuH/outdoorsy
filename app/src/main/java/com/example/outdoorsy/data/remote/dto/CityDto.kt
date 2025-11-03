package com.example.outdoorsy.data.remote.dto

import com.example.outdoorsy.domain.model.City

data class CityDto(
    val id: Int,
    val name: String,
    val coord: CoordDto,
    val country: String,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

fun CityDto.toDomain(): City = City(
    id = id,
    name = name,
    country = country,
    coordinates = coord.toDomain(),
    timezone = timezone,
    sunrise = sunrise,
    sunset = sunset
)
