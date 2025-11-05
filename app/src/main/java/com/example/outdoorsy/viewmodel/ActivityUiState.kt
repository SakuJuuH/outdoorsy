package com.example.outdoorsy.viewmodel

data class ActivityUiState(
    val locations: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val selectedLocation: String = "",
    val selectedActivity: String = ""
)