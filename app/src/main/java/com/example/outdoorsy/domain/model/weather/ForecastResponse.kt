package com.example.outdoorsy.domain.model.weather

import com.example.outdoorsy.domain.model.weather.components.City
import com.example.outdoorsy.domain.model.weather.components.ForecastItem

data class ForecastResponse(val listOfForecastItems: List<ForecastItem>, val city: City)
