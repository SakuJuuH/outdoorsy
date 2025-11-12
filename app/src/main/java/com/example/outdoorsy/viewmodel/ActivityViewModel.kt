package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.outdoorsy.data.test.ActivitiesData
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ActivityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ActivityUiState(
            // TODO: Replace location placeholder values with proper database queries
            locations = listOf("Helsinki", "Espoo", "Vantaa"),
            activities = ActivitiesData.activities
        )
    )
    val uiState: StateFlow<ActivityUiState> = _uiState

    fun updateLocation(newLocation: String) {
        _uiState.value = _uiState.value.copy(selectedLocation = newLocation)
    }

    fun updateActivity(newActivity: String) {
        _uiState.value = _uiState.value.copy(selectedActivity = newActivity)
    }

    fun updateStartTime(newTime: LocalTime, endTime: LocalTime) {
        _uiState.update {
            it.copy(
                selectedStartTime = newTime,
                timeRangeError = null
            )
        }

        if (endTime.isBefore(newTime)) {
            val errorMessage = "End time was automatically adjusted."
            val adjustedEndTime = newTime.plusHours(1)

            _uiState.update {
                it.copy(
                    selectedEndTime = adjustedEndTime,
                    timeRangeError = errorMessage
                )
            }
        } else {
            _uiState.update {
                it.copy(timeRangeError = null)
            }
        }
    }

    fun updateEndTime(newTime: LocalTime, startTime: LocalTime) {
        if (newTime.isBefore(startTime)) {
            val errorMessage = "End time must be after start time."

            _uiState.update {
                it.copy(
                    timeRangeError = errorMessage
                )
            }

        } else {
            _uiState.update {
                it.copy(
                    selectedEndTime = newTime,
                    timeRangeError = null
                )
            }
        }
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
