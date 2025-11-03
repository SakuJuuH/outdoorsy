package com.example.outdoorsy.domain.model

data class ForecastResponse(
    val count: Int,
    val listOfForecastItems: List<ForecastItem>,
    val city: City
)
