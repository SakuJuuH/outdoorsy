package com.example.outdoorsy.ui.settings

data class SettingsUiState(
    val temperatureUnit: String = "",
    val language: String = "",
    val isDarkMode: Boolean = false,
    val currency: String = ""
)
