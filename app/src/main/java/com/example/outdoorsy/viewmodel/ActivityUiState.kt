package com.example.outdoorsy.viewmodel

import java.time.LocalTime

data class ActivityUiState(
    val locations: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val selectedLocation: String = "",
    val selectedActivity: String = "",
    val selectedTime: LocalTime = LocalTime.now()
)