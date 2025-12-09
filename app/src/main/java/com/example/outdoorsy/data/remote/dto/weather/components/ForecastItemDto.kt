package com.example.outdoorsy.data.remote.dto.weather.components

import com.example.outdoorsy.domain.model.weather.components.ForecastItem

data class ForecastItemDto(
    val dt: Int,
    val main: MainDto,
    val weather: List<WeatherDto>,
    val wind: WindDto,
    val pop: Double
) {
    fun toDomain(): ForecastItem = ForecastItem(
        timeOfData = dt,
        main = main.toDomain(),
        weather = weather.map { it.toDomain() },
        wind = wind.toDomain(),
        pop = pop
    )
}
