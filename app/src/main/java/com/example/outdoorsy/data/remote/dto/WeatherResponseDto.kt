package com.example.outdoorsy.data.remote.dto

import com.example.outdoorsy.domain.model.WeatherResponse

data class WeatherResponseDto(
    val coord: CoordDto,
    val weather: List<WeatherDto>,
    val base: String,
    val main: MainDto,
    val visibility: Int,
    val wind: WindDto,
    val clouds: CloudsDto,
    val dt: Long,
    val sys: WeatherSysDto,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

fun WeatherResponseDto.toDomain(): WeatherResponse = WeatherResponse(
    coord.toDomain(),
    weather = weather.map { it.toDomain() },
    main = main.toDomain(),
    visibility = visibility,
    wind = wind.toDomain(),
    clouds = clouds.toDomain(),
    timeOfData = dt,
    sys = sys.toDomain(),
    timezone = timezone,
    name = name
)
