package com.example.outdoorsy.data.remote.dto.assistant

import com.google.gson.annotations.SerializedName

data class AiAssistantResponseDto(
    val answer: String,
    val data: Map<String, String>?,
    @SerializedName("session_id")
    val sessionId: String
)
