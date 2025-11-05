package com.example.outdoorsy.utils

const val BASE_URL = "https://api.openweathermap.org/"

object TemperatureSystem {
    const val METRIC = "metric"
    const val IMPERIAL = "imperial"

    val DISPLAY_NAMES = mapOf(
        METRIC to "Metric (°C)",
        IMPERIAL to "Imperial (°F)"
    )
}

object Language {
    const val ENGLISH = "en"

    val DISPLAY_NAMES = mapOf(
        ENGLISH to "English"
    )
}
