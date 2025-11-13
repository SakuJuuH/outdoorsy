package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantRequestDto
import com.example.outdoorsy.data.repository.ActivityRepositoryImpl
import com.example.outdoorsy.data.test.ActivitiesData
import com.example.outdoorsy.data.test.WeatherPromptProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(private val repository: ActivityRepositoryImpl) : ViewModel() {
    private val _uiState = MutableStateFlow(
        ActivityUiState(
            // TODO: Replace location placeholder values with proper database queries
            locations = listOf("Helsinki", "Espoo", "Vantaa"),
            activities = ActivitiesData.activities
        )
    )
    val uiState: StateFlow<ActivityUiState> = _uiState

    fun updateLocation(newLocation: String) {
        _uiState.update {
            it.copy(
                selectedLocation = newLocation
            )
        }
    }

    fun updateActivity(newActivity: String) {
        _uiState.update {
            it.copy(
                selectedActivity = newActivity
            )
        }
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

    suspend fun performSearch() {
        val activity = _uiState.value.selectedActivity
        val location = _uiState.value.selectedLocation
        val date = LocalDate.now().toString()
        val startTime = _uiState.value.selectedStartTime.toString()
        val endTime = _uiState.value.selectedEndTime.toString()

        Log.d("ActivitySearch", "Searching for $activity in $location from $startTime to $endTime in $date")
        val prompt = WeatherPromptProvider.buildPrompt(activity, location, date, startTime, endTime)
        val request = AiAssistantRequestDto(prompt)

        val response = repository.startAiSession(request)
        val locationEntry = response.data?.entries?.firstOrNull()

        // TODO: Continue validation logic, improve prompt and update UI with data
        if (locationEntry != null) {
            val locationName = locationEntry.key
            val weatherData = locationEntry.value

            _uiState.update {
                it.copy(
                    searchPerformed = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    searchPerformed = false
                )
            }
        }
    }
}

// TODO: Add a proper DTO for the LLM answer based on the prompt
//fun parseAnswerJson(answer: String): RecommendationDto {
//    val jsonStart = answer.indexOf('{')
//    val jsonEnd = answer.lastIndexOf('}')
//    val jsonBlock = answer.substring(jsonStart, jsonEnd + 1)
//
//    return Gson().fromJson(jsonBlock, RecommendationDto::class.java)
//}
