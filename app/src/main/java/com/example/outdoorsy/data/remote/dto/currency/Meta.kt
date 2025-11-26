package com.example.outdoorsy.data.remote.dto.currency

import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("last_updated_at")
    val lastUpdatedAt: String
)
