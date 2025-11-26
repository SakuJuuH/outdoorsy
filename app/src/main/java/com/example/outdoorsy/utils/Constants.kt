package com.example.outdoorsy.utils

const val OWM_BASE_URL = "https://api.openweathermap.org/"
const val EBAY_BASE_URL = "https://api.ebay.com"
const val CURRENCY_BASE_URL = "https://api.currencyapi.com/"

enum class TemperatureSystem(val code: String, val displayName: String, val symbol: String) {
    METRIC("metric", "Metric (°C)", "°C"),
    IMPERIAL("imperial", "Imperial (°F)", "°F");

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

enum class Currencies(val code: String, val displayName: String, val symbol: String) {
    USD("USD", "US Dollar ($)", "$"),
    EUR("EUR", "Euro (€)", "€"),
    GBP("GBP", "British Pound (£)", "£");

    companion object {
        fun fromCode(code: String): Currencies = entries.find { it.code == code } ?: GBP
    }
}
