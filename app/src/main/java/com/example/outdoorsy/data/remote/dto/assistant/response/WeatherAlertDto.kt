package com.example.outdoorsy.data.remote.dto.assistant.response

import com.google.gson.annotations.SerializedName

data class WeatherAlertDto(
    @SerializedName("sender_name")
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)