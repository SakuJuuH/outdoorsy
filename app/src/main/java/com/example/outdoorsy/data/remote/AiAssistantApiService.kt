package com.example.outdoorsy.data.remote

import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantRequestDto
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiAssistantApiService {
    /**
     * Starts a new session with the AI Weather Assistant.
     *
     * @param apiKey Your OpenWeatherMap API key, passed in the 'X-Api-Key' Header.
     * @param request The JSON body containing the "prompt" (user query).
     * @return The AI Assistant's response, including the generated answer and weather data.
     */
    @POST("assistant/session")
    suspend fun startAiSession(
        @Header("X-Api-Key") apiKey: String,
        @Body request: AiAssistantRequestDto
    ): AiAssistantResponseDto
}