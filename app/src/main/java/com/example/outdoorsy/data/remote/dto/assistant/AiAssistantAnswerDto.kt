package com.example.outdoorsy.data.remote.dto.assistant

import com.google.gson.annotations.SerializedName

// Has to be parsed separately from the AiAssistantResponseDto.answer
data class AiAssistantAnswerDto(
    val location: String,
    val date: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    val activity: String,
    @SerializedName("suitability_score") val suitabilityScore: String,
    @SerializedName("suitability_info") val suitabilityInfo: List<String>,
    @SerializedName("clothing_tips") val clothingTips: List<String>,
    @SerializedName("clothing_items") val clothingItems: List<String>,
    @SerializedName("weather_tips") val weatherTips: List<String>
)
