package com.example.outdoorsy.domain.model.weather.components

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)
