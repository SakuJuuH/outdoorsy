package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantAnswerDto
import com.example.outdoorsy.data.test.ActivitiesData
import com.example.outdoorsy.data.test.WeatherPromptProvider
import com.example.outdoorsy.domain.usecase.GetAiAssistant
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ActivityViewModel @Inject constructor(private val getAiAssistant: GetAiAssistant) :
    ViewModel() {
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

    // TODO: Remove Log statements
    fun performSearch() {
        val activity = _uiState.value.selectedActivity
        val location = _uiState.value.selectedLocation
        val date = LocalDate.now().toString()
        val startTime = _uiState.value.selectedStartTime.toString()
        val endTime = _uiState.value.selectedEndTime.toString()

        Log.d(
            "ActivitySearch",
            "Searching for $activity in $location from $startTime to $endTime in $date"
        )
        val prompt = WeatherPromptProvider.buildPrompt(activity, location, date, startTime, endTime)

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        searchPerformed = null,
                        isLoading = true
                    )
                }

                val response = getAiAssistant(prompt)
                Log.d("Response", "$response")
                val aiAnswer = Gson().fromJson(response.answer, AiAssistantAnswerDto::class.java)

                _uiState.update {
                    it.copy(
                        searchPerformed = true,
                        isLoading = false,
                        aiAnswer = aiAnswer
                    )
                }

                Log.d("UI State", uiState.value.aiAnswer.toString())
            } catch (e: Exception) {
                Log.e("ActivitySearch", "Error calling AI Assistant", e)
                _uiState.update {
                    it.copy(
                        searchPerformed = false,
                        isLoading = false
                    )
                }
            }
        }
    }
}