package com.example.outdoorsy.domain.model.weather

data class ForecastItem(
    val timeOfData: Int,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val clouds: Clouds,
    val pop: Double,
    val rain: Rain?,
    val sys: ForecastSys
)
