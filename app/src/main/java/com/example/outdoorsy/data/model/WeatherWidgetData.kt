package com.example.outdoorsy.data.model

data class WeatherWidgetData(
    val location: String = "No Location",
    val temperature: Int = 0,
    val condition: String = "Unknown",
    val high: Int = 0,
    val low: Int = 0,
    val icon: String = "",
    val unit: String = "metric"
)