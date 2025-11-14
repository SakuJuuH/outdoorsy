package com.example.outdoorsy.data.remote

import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantRequestDto
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AiAssistantApiService {
    @POST("assistant/session")
    suspend fun startAiSession(@Body request: AiAssistantRequestDto): AiAssistantResponseDto
}
