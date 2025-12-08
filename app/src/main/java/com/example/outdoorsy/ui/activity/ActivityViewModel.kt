package com.example.outdoorsy.ui.activity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.R
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantAnswerDto
import com.example.outdoorsy.domain.model.Activity
import com.example.outdoorsy.domain.model.ActivityLog
import com.example.outdoorsy.domain.model.Location
import com.example.outdoorsy.domain.model.weather.ForecastResponse
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import com.example.outdoorsy.domain.repository.ActivityRepository
import com.example.outdoorsy.domain.repository.LocationRepository
import com.example.outdoorsy.domain.repository.SettingsRepository
import com.example.outdoorsy.domain.usecase.GetAiAssistant
import com.example.outdoorsy.domain.usecase.GetForecast
import com.example.outdoorsy.utils.WeatherPromptProvider
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val getAiAssistant: GetAiAssistant,
    private val getForecast: GetForecast,
    private val settingsRepository: SettingsRepository,
    private val activityLogRepository: ActivityLogRepository,
    private val activityRepository: ActivityRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(value = ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState

    init {
        viewModelScope.launch {
            activityRepository.getAllActivities().collect { activities ->
                _uiState.update { it.copy(activities = activities) }
            }
        }
        viewModelScope.launch {
            locationRepository.getAllLocations().collect { locations ->
                _uiState.update { it.copy(locations = locations) }
            }
        }
    }

    fun updateLocation(newLocation: String) {
        val locations = _uiState.value.locations
        val matched: Location? = if (!newLocation.contains("Unknown")) {
            locations.firstOrNull { it.name == newLocation }
        } else {
            locations.firstOrNull { "Unknown: ${it.latitude} ${it.longitude}" == newLocation }
        }

        _uiState.update {
            it.copy(
                selectedLocation = matched
            )
        }
    }

    fun updateActivity(newActivity: String) {
        val activities = _uiState.value.activities
        val matched = activities.firstOrNull { it.name == newActivity }
        _uiState.update {
            it.copy(selectedActivity = matched)
        }
    }

    fun updateStartDateTime(
        newDate: LocalDate,
        newTime: LocalTime,
        endDate: LocalDate,
        endTime: LocalTime
    ) {
        _uiState.update {
            it.copy(
                selectedStartDate = newDate,
                selectedStartTime = newTime,
                timeRangeErrorId = null
            )
        }

        if (endDate.isBefore(newDate) || (endDate.isEqual(newDate) && endTime.isBefore(newTime))) {
            val newEndDateTime = LocalDateTime.of(newDate, newTime).plusHours(1)

            _uiState.update {
                it.copy(
                    selectedEndDate = newEndDateTime.toLocalDate(),
                    selectedEndTime = newEndDateTime.toLocalTime(),
                    timeRangeErrorId = R.string.time_error_adjusted
                )
            }
        } else {
            _uiState.update {
                it.copy(timeRangeErrorId = null)
            }
        }
    }

    fun updateEndDateTime(
        newDate: LocalDate,
        newTime: LocalTime,
        startDate: LocalDate,
        startTime: LocalTime
    ) {
        if (newDate.isBefore(startDate) ||
            (newDate.isEqual(startDate) && newTime.isBefore(startTime))
        ) {
            _uiState.update {
                it.copy(
                    timeRangeErrorId = R.string.time_error_invalid
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    selectedEndDate = newDate,
                    selectedEndTime = newTime,
                    timeRangeErrorId = null
                )
            }
        }
    }

    fun addActivity(newActivity: String) {
        val activities = _uiState.value.activities
        val matched = activities.firstOrNull { it.name == newActivity }

        if (matched == null) {
            viewModelScope.launch(Dispatchers.IO) {
                activityRepository.saveActivity(Activity(name = newActivity))
            }
        }
    }

    fun updateNewActivityName(newActivity: String) {
        _uiState.update {
            it.copy(
                newActivityName = newActivity
            )
        }
    }

    fun deleteActivity(activityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val matched = _uiState.value.activities.firstOrNull { it.name == activityName }

            if (matched == _uiState.value.selectedActivity) {
                _uiState.update {
                    it.copy(
                        selectedActivity = null
                    )
                }
            }

            matched?.let {
                activityRepository.deleteActivityByName(it)
            }
        }
    }

    fun updateShowDialog(value: Boolean) {
        _uiState.update {
            it.copy(
                showActivityDialog = value
            )
        }
    }

    // TODO: Remove Log statements
    fun performSearch() {
        val activity = _uiState.value.selectedActivity
        val location = _uiState.value.selectedLocation
        val startDate = _uiState.value.selectedStartDate
        val endDate = _uiState.value.selectedEndDate
        val startTime = _uiState.value.selectedStartTime
        val endTime = _uiState.value.selectedEndTime

        viewModelScope.launch {
            var forecast: ForecastResponse? = null
            try {
                forecast = getForecast(
                    lat = location?.latitude,
                    lon = location?.longitude,
                    city = location?.name,
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
                activity = activity.name,
                location = location.name ?: "Unknown",
                startDate = startDate.toString(),
                endDate = endDate.toString(),
                startTime = startTime.toString(),
                endTime = endTime.toString(),
                forecast = forecast.toString(),
                unit = settingsRepository.getTemperatureUnit().first(),
                language = settingsRepository.getLanguage().first()
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
                    location = location.name ?: "Unknown",
                    activityName = activity.name,
                    startDateTime = LocalDateTime.of(startDate, startTime),
                    endDateTime = LocalDateTime.of(endDate, endTime),
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
