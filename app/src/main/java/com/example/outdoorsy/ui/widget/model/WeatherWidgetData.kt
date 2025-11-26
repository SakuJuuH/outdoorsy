package com.example.outdoorsy.ui.widget.model

data class WeatherWidgetData(
    val location: String,
    val temperature: Int,
    val condition: String,
    val high: Int = 0,
    val low: Int = 0,
    val icon: String,
    val isCurrentLocation: Boolean
)
