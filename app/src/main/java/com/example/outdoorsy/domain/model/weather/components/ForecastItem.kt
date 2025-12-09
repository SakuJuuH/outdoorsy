package com.example.outdoorsy.domain.model.weather.components

data class ForecastItem(
    val timeOfData: Int,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val pop: Double
)
