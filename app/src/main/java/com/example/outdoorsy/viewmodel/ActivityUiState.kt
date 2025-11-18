package com.example.outdoorsy.viewmodel

import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantAnswerDto
import java.time.LocalTime

data class ActivityUiState(
    val locations: List<String> = emptyList(),
    val activities: List<String> = emptyList(),
    val selectedLocation: String = "",
    val selectedActivity: String = "",
    val selectedStartTime: LocalTime = LocalTime.now(),
    val selectedEndTime: LocalTime = LocalTime.now().plusHours(1),
    val timeRangeError: String? = null,
    // true = search successful, false = search error, null = search not performed
    val searchPerformed: Boolean? = null,
    val isLoading: Boolean = false,
    val aiAnswer: AiAssistantAnswerDto? = null
)
