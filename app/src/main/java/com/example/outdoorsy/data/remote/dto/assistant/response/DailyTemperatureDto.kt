package com.example.outdoorsy.data.remote.dto.assistant.response

data class DailyTemperatureDto(
    val morn: Double,
    val day: Double,
    val eve: Double,
    val night: Double,
    val min: Double? = null,
    val max: Double? = null
)
