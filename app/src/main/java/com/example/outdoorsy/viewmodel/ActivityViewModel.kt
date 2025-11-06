package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import java.time.LocalTime
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

    fun updateTime(time: LocalTime) {
        _uiState.value = _uiState.value.copy(selectedTime = time)
    }

    fun performSearch() {
        val location = _uiState.value.selectedLocation
        val activity = _uiState.value.selectedActivity
        val time = _uiState.value.selectedTime

        // Perform search logic here
        Log.d("ActivitySearch", "Searching for $activity in $location at $time")
    }
}
