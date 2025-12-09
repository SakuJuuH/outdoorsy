package com.example.outdoorsy.data.remote.dto.weather.components

import com.example.outdoorsy.domain.model.weather.components.City

data class CityDto(
    val id: Int,
    val name: String,
    val country: String,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
) {
    fun toDomain(): City = City(
        id = id,
        name = name,
        country = country,
        timezone = timezone,
        sunrise = sunrise,
        sunset = sunset
    )
}
