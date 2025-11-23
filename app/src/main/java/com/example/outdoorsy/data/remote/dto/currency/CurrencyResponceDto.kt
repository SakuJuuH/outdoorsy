package com.example.outdoorsy.data.remote.dto.currency

import com.google.gson.annotations.SerializedName

data class CurrencyResponseDto(
    val meta: Meta,
    val data: Map<String, CurrencyDataDto>
)

data class Meta(
    @SerializedName("last_updated_at")
    val lastUpdatedAt: String
)

data class CurrencyDataDto(
    val code: String,
    val value: Double
)
