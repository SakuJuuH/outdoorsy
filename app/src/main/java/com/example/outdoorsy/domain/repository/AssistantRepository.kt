package com.example.outdoorsy.domain.repository

import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantRequestDto
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantResponseDto

interface AssistantRepository {
    suspend fun startAiSession(request: AiAssistantRequestDto): AiAssistantResponseDto
}
