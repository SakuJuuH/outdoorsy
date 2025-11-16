package com.example.outdoorsy.data.remote.dto.ebay

import com.google.gson.annotations.SerializedName

data class EbayTokenResponseDto(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("token_type")
    val tokenType: String
)
