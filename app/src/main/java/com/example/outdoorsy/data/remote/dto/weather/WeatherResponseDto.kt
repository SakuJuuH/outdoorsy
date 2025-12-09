package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.data.remote.dto.weather.components.MainDto
import com.example.outdoorsy.data.remote.dto.weather.components.WeatherDto
import com.example.outdoorsy.data.remote.dto.weather.components.WeatherSysDto
import com.example.outdoorsy.data.remote.dto.weather.components.WindDto
import com.example.outdoorsy.domain.model.weather.WeatherResponse

data class WeatherResponseDto(
    val weather: List<WeatherDto>,
    val main: MainDto,
    val visibility: Int,
    val wind: WindDto,
    val dt: Long,
    val sys: WeatherSysDto,
    val timezone: Int,
    val name: String
) {
    fun toDomain() = WeatherResponse(
        weather = weather.map { it.toDomain() },
        main = main.toDomain(),
        visibility = visibility,
        wind = wind.toDomain(),
        timeOfData = dt,
        sys = sys.toDomain(),
        timezone = timezone,
        name = name
    )
}
