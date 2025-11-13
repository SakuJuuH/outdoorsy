package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.remote.AiAssistantApiService
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantRequestDto
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantResponseDto
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val assistantService: AiAssistantApiService
) : AiAssistantApiService {
    override suspend fun startAiSession(request: AiAssistantRequestDto): AiAssistantResponseDto =
        assistantService.startAiSession(request)
}
