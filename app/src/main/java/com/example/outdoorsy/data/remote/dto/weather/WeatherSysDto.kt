package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.domain.model.weather.WeatherSys

data class WeatherSysDto(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

fun WeatherSysDto.toDomain(): WeatherSys = WeatherSys(
    country = country,
    sunrise = sunrise,
    sunset = sunset
)
