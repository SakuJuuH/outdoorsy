package com.example.outdoorsy.ui.activity

import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantAnswerDto
import com.example.outdoorsy.domain.model.Activity
import com.example.outdoorsy.domain.model.Location
import java.time.LocalTime
import java.time.LocalDate
import java.time.LocalDateTime

val timeNow: LocalDateTime = LocalDateTime.now()
val timePlusOne: LocalDateTime = timeNow.plusHours(1)

data class ActivityUiState(
    val locations: List<Location> = emptyList(),
    val activities: List<Activity> = emptyList(),
    val selectedLocation: Location? = null,
    val selectedActivity: Activity? = null,
    val selectedStartTime: LocalTime = timeNow.toLocalTime(),
    val selectedEndTime: LocalTime = timePlusOne.toLocalTime(),
    val selectedStartDate: LocalDate = timeNow.toLocalDate(),
    val selectedEndDate: LocalDate = timePlusOne.toLocalDate(),
    val timeRangeErrorId: Int? = null,
    // true = search successful, false = search error, null = search not performed
    val searchPerformed: Boolean? = null,
    val isLoading: Boolean = false,
    val aiAnswer: AiAssistantAnswerDto? = null,
    val showActivityDialog: Boolean = false,
    val newActivityName: String = ""
)
