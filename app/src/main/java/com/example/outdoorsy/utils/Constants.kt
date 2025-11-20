package com.example.outdoorsy.utils

const val OWM_BASE_URL = "https://api.openweathermap.org/"
const val EBAY_BASE_URL = "https://api.ebay.com"

enum class TemperatureSystem(val code: String, val displayName: String) {
    METRIC("metric", "Metric (°C)"),
    IMPERIAL("imperial", "Imperial (°F)");

    companion object {
        fun fromCode(code: String): TemperatureSystem = entries.find { it.code == code } ?: METRIC
    }
}

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    FINNISH("fi");

    companion object {
        fun fromCode(code: String): AppLanguage = entries.find { it.code == code } ?: ENGLISH
    }
}
