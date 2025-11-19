package com.example.outdoorsy.data.remote.dto.geocoding

import com.example.outdoorsy.domain.model.Location as LocationModel
import com.google.gson.annotations.SerializedName

data class GeocodingDto(
    val name: String,
    @SerializedName("local_names")
    val localNames: Map<String, String>?,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String?
)

fun GeocodingDto.toDomain(): LocationModel = LocationModel(
    name = name,
    country = country,
    state = state,
    latitude = lat,
    longitude = lon
)
