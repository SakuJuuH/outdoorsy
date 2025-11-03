package com.example.outdoorsy.data.remote.dto

import com.example.outdoorsy.domain.model.ForecastItem
import com.google.gson.annotations.SerializedName

data class ForecastItemDto(
    val dt: Int,
    val main: MainDto,
    val weather: List<WeatherDto>,
    val clouds: CloudsDto,
    val wind: WindDto,
    val visibility: Int,
    val pop: Double,
    val rain: RainDto?,
    val sys: ForecastSysDto,
    @SerializedName("dt_txt")
    val dtTxt: String
)

fun ForecastItemDto.toDomain(): ForecastItem = ForecastItem(
    timeOfData = dt,
    main = main.toDomain(),
    weather = weather.map { it.toDomain() },
    wind = wind.toDomain(),
    clouds = clouds.toDomain(),
    pop = pop,
    rain = rain?.toDomain(),
    sys = sys.toDomain()
)
