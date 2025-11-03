package com.example.outdoorsy.domain.model

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val timeOfData: Long,
    val sys: WeatherSys,
    val timezone: Int,
    val name: String
)
