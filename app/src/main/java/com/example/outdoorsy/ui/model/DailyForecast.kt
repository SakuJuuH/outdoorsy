package com.example.outdoorsy.ui.model

data class DailyForecast(
    val day: String,
    val high: Int,
    val low: Int,
    val condition: String,
    val icon: String
)
