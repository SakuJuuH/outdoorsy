package com.example.outdoorsy.domain.model

data class City(
    val id: Int,
    val name: String,
    val country: String,
    val coordinates: Coord,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)
