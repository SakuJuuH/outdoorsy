package com.example.outdoorsy.data.remote.dto.weather.components

import com.example.outdoorsy.domain.model.weather.components.WeatherSys

data class WeatherSysDto(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long
) {
    fun toDomain(): WeatherSys = WeatherSys(
        country = country,
        sunrise = sunrise,
        sunset = sunset
    )
}
