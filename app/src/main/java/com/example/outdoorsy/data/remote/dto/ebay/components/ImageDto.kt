package com.example.outdoorsy.data.remote.dto.ebay.components

import com.google.gson.annotations.SerializedName

data class ImageDto(
    @SerializedName("imageUrl")
    val url: String
)
