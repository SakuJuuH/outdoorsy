package com.example.outdoorsy.data.remote.dto.assistant.response

import com.google.gson.annotations.SerializedName

data class PrecipitationDto(
    @SerializedName("1h")
    val oneHour: Double?
)