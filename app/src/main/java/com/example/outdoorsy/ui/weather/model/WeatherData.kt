package com.example.outdoorsy.ui.weather.model

data class WeatherData(
    val location: String,
    val temp: Int,
    val condition: String,
    val high: Int,
    val low: Int,
    val humidity: Int,
    val windSpeed: Int,
    val visibility: Double,
    val pressure: Double,
    val sunrise: String,
    val sunset: String,
    val forecast: List<DailyForecast>,
    val icon: String,
    val isCurrentLocation: Boolean = false,
    val unit: String
)
