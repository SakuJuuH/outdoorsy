package com.example.outdoorsy.data.remote.dto.assistant.response

data class WeatherConditionDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
