package com.example.outdoorsy.ui.activity

import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantAnswerDto
import com.example.outdoorsy.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalTime

data class ActivityUiState(
    val locations: List<String> = emptyList(),
    val activities: Flow<List<Activity>> = flowOf(emptyList()),
    val selectedLocation: String? = null,
    val selectedActivity: Activity? = null,
    val selectedStartTime: LocalTime = LocalTime.now(),
    val selectedEndTime: LocalTime = LocalTime.now().plusHours(1),
    val timeRangeErrorId: Int? = null,
    // true = search successful, false = search error, null = search not performed
    val searchPerformed: Boolean? = null,
    val isLoading: Boolean = false,
    val aiAnswer: AiAssistantAnswerDto? = null
)
