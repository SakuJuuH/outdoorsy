package com.example.outdoorsy.domain.model.weather.components

data class Main(
    val temperature: Double,
    val feelsLikeTemperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: Int,
    val seaLevel: Int,
    val groundLevel: Int,
    val humidity: Int
)
