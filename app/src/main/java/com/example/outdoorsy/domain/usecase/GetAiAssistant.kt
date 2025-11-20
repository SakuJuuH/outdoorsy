package com.example.outdoorsy.domain.usecase

import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantRequestDto
import com.example.outdoorsy.data.remote.dto.assistant.AiAssistantResponseDto
import com.example.outdoorsy.domain.repository.AssistantRepository
import javax.inject.Inject

class GetAiAssistant @Inject constructor(
    private val repository: AssistantRepository
) {
    suspend operator fun invoke(prompt: String): AiAssistantResponseDto {
        val requestDto = AiAssistantRequestDto(prompt = prompt)
        return repository.startAiSession(requestDto)
    }
}
