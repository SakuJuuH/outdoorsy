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

    fun updateStartTime(time: LocalTime) {
        _uiState.value = _uiState.value.copy(selectedStartTime = time)
    }

    fun updateEndTime(time: LocalTime) {
        _uiState.value = _uiState.value.copy(selectedEndTime = time)
    }

    fun performSearch() {
        val location = _uiState.value.selectedLocation
        val activity = _uiState.value.selectedActivity
        val startTime = _uiState.value.selectedStartTime
        val endTime = _uiState.value.selectedEndTime

        // Perform search logic here
        Log.d("ActivitySearch", "Searching for $activity in $location from $startTime to $endTime")
    }
}
