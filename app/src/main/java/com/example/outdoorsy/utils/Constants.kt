package com.example.outdoorsy.utils

import com.example.outdoorsy.R

const val OWM_BASE_URL = "https://api.openweathermap.org/"
const val EBAY_BASE_URL = "https://api.ebay.com"
const val CURRENCY_BASE_URL = "https://api.currencyapi.com/"

enum class TemperatureSystem(val code: String, val displayName: Int, val symbol: String) {
    METRIC("metric", R.string.unit_metric, "°C"),
    IMPERIAL("imperial", R.string.unit_imperial, "°F");

    companion object {
        fun fromCode(code: String): TemperatureSystem = entries.find { it.code == code } ?: METRIC
    }
}

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    FINNISH("fi")
}

enum class Currencies(val code: String, val displayName: Int, val symbol: String) {
    USD("USD", R.string.currency_usd, "$"),
    EUR("EUR", R.string.currency_eur, "€"),
    GBP("GBP", R.string.currency_gbp, "£");

    companion object {
        fun fromCode(code: String): Currencies = entries.find { it.code == code } ?: GBP
    }
}

enum class AppTheme(val code: String, val displayName: Int) {
    LIGHT("light", R.string.light_theme),
    DARK("dark", R.string.dark_theme),
    SYSTEM("system", R.string.use_device_theme);

    companion object {
        fun fromCode(code: String): AppTheme = entries.find { it.code == code } ?: SYSTEM
    }
}
