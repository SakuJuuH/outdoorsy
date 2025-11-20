package com.example.outdoorsy.data.remote.dto.ebay.components

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("categoryId")
    val id: String,
    @SerializedName("categoryName")
    val name: String
)

fun CategoryDto.toDomain() = this.name
