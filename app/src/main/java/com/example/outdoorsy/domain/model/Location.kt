package com.example.outdoorsy.domain.model

import com.example.outdoorsy.data.local.entity.Location

data class Location(
    val name: String? = null,
    val country: String? = null,
    val state: String? = null,
    val latitude: Double,
    val longitude: Double
) {
    fun toEntity(): Location = Location(
        name = name?.replaceFirstChar { it.uppercase() } ?: "Unknown",
        country = country ?: "",
        state = state,
        latitude = latitude,
        longitude = longitude
    )
}
