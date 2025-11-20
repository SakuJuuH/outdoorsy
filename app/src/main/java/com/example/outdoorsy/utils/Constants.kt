package com.example.outdoorsy.utils

const val OWM_BASE_URL = "https://api.openweathermap.org/"
const val EBAY_BASE_URL = "https://api.ebay.com"

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
    const val FINNISH = "fi"
}
