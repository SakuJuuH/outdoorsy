package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.remote.AiAssistantApiService
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantRequestDto
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantResponseDto
import com.example.outdoorsy.domain.repository.AssistantRepository
import javax.inject.Inject

class AssistantRepositoryImpl @Inject constructor(
    private val assistantService: AiAssistantApiService
) : AssistantRepository {
    override suspend fun startAiSession(request: AiAssistantRequestDto): AiAssistantResponseDto =
        assistantService.startAiSession(request)
}
