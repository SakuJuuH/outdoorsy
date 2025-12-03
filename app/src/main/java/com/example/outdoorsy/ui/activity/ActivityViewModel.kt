package com.example.outdoorsy.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.R
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantAnswerDto
import com.example.outdoorsy.data.repository.SettingsRepository
import com.example.outdoorsy.domain.model.Activity
import com.example.outdoorsy.domain.model.ActivityLog
import com.example.outdoorsy.domain.model.weather.ForecastResponse
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import com.example.outdoorsy.domain.repository.ActivityRepository
import com.example.outdoorsy.domain.usecase.GetAiAssistant
import com.example.outdoorsy.domain.usecase.GetForecast
import com.example.outdoorsy.utils.WeatherPromptProvider
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getAiAssistant: GetAiAssistant,
    private val getForecast: GetForecast,
    private val settingsRepository: SettingsRepository,
    private val activityLogRepository: ActivityLogRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {
    init {
        // TODO: Replace with proper automatic DB check and insertion or user input-driven insertion
        viewModelScope.launch(Dispatchers.IO) {
            try {
                activityRepository.saveActivity(Activity("Hiking"))
                activityRepository.saveActivity(Activity("Gardening"))
                activityRepository.saveActivity(Activity("Camping"))
            } catch (e: Exception) {
                Log.e("Insertion Error", e.toString())
            }
        }
    }

    private val _uiState = MutableStateFlow(
        ActivityUiState(
            // TODO: Replace location placeholder values with proper database queries
            locations = listOf("Helsinki", "Espoo", "Vantaa"),
            activities = activityRepository.getAllActivities()
        )
    )
    val uiState: StateFlow<ActivityUiState> = _uiState

    fun updateLocation(newLocation: String) {
        val locations = _uiState.value.locations
        val matched = locations.firstOrNull { it == newLocation }
        _uiState.update {
            it.copy(
                selectedLocation = matched
            )
        }
    }

    fun updateActivity(newActivity: String) {
        val activities = _uiState.value.activities
        viewModelScope.launch {
            activities.collect { activities ->
                val matched = activities.firstOrNull { it.name == newActivity }
                _uiState.update {
                    it.copy(selectedActivity = matched)
                }
            }
        }
    }

    fun updateStartTime(newTime: LocalTime, endTime: LocalTime) {
        _uiState.update {
            it.copy(
                selectedStartTime = newTime,
                timeRangeErrorId = null
            )
        }

        if (endTime.isBefore(newTime)) {
            val adjustedEndTime = newTime.plusHours(1)

            _uiState.update {
                it.copy(
                    selectedEndTime = adjustedEndTime,
                    timeRangeErrorId = R.string.activity_screen_time_error_adjusted
                )
            }
        } else {
            _uiState.update {
                it.copy(timeRangeErrorId = null)
            }
        }
    }

    fun updateEndTime(newTime: LocalTime, startTime: LocalTime) {
        if (newTime.isBefore(startTime)) {
            _uiState.update {
                it.copy(
                    timeRangeErrorId = R.string.activity_screen_time_error_invalid
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    selectedEndTime = newTime,
                    timeRangeErrorId = null
                )
            }
        }
    }

    // TODO: Remove Log statements
    fun performSearch() {
        val activity = _uiState.value.selectedActivity
        val location = _uiState.value.selectedLocation
        val date = LocalDate.now()
        val startTime = _uiState.value.selectedStartTime
        val endTime = _uiState.value.selectedEndTime

        viewModelScope.launch {
            var forecast: ForecastResponse? = null
            try {
                forecast = getForecast(
                    city = location,
                    units = "metric",
                    language = "en"
                )
            } catch (e: Exception) {
                Log.e("Forecast", "Error fetching forecast", e)
            }

            if (activity == null || location == null) {
                throw IllegalArgumentException("Activity must not be null")
            }

            val prompt = WeatherPromptProvider.buildPrompt(
                activity.name,
                location,
                date.toString(),
                startTime.toString(),
                endTime.toString(),
                forecast.toString(),
                settingsRepository.temperatureUnit.first(),
                settingsRepository.language.first()
            )

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
                activityRepository.setClothingItems(aiAnswer.clothingItems)

                val activityLog = ActivityLog(
                    location = location,
                    activityName = activity.name,
                    startDateTime = LocalDateTime.of(date, startTime),
                    endDateTime = LocalDateTime.of(date, endTime),
                    suitabilityLabel = aiAnswer.suitabilityLabel,
                    suitabilityScore = aiAnswer.suitabilityScore
                )
                withContext(Dispatchers.IO) {
                    activityLogRepository.saveActivityLog(activityLog)
                }

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
