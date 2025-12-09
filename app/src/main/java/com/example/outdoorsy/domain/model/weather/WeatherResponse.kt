package com.example.outdoorsy.domain.model.weather

import com.example.outdoorsy.domain.model.weather.components.Main
import com.example.outdoorsy.domain.model.weather.components.Weather
import com.example.outdoorsy.domain.model.weather.components.WeatherSys
import com.example.outdoorsy.domain.model.weather.components.Wind

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val timeOfData: Long,
    val sys: WeatherSys,
    val timezone: Int,
    val name: String
)
