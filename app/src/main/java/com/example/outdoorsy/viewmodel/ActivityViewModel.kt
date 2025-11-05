package com.example.outdoorsy.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ActivityUiState(
            // TODO: Replace placeholder values with proper database queries
            locations = listOf("Helsinki", "Espoo", "Vantaa"),
            activities = listOf("Hiking", "Gardening", "Cycling", "Swimming")
        )
    )
    val uiState: StateFlow<ActivityUiState> = _uiState

    fun updateLocation(newLocation: String) {
        _uiState.value = _uiState.value.copy(selectedLocation = newLocation)
    }

    fun updateActivity(newActivity: String) {
        _uiState.value = _uiState.value.copy(selectedActivity = newActivity)
    }
}
