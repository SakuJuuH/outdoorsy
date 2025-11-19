package com.example.outdoorsy.domain.model

data class Location(
    val name: String? = null,
    val country: String? = null,
    val state: String? = null,
    val latitude: Double,
    val longitude: Double
)
