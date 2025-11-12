package com.example.outdoorsy.data.remote.dto.assistant

import com.example.outdoorsy.data.remote.dto.assistant.response.LocationWeatherDataDto
import com.google.gson.annotations.SerializedName

data class AiAssistantResponseDto(
    val answer: String,
    val data: Map<String, LocationWeatherDataDto>?,
    @SerializedName("session_id")
    val sessionId: String
)
