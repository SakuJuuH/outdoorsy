package com.example.outdoorsy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.outdoorsy.domain.model.Location as LocationModel

@Entity(
    tableName = "location",
    indices = [Index(value = ["name"], unique = true)]
)
data class Location(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val country: String = "",
    val state: String? = null,
    val latitude: Double,
    val longitude: Double
)

fun Location.toDomain(): LocationModel = LocationModel(
    name = name.replaceFirstChar { it.uppercase() },
    country = country,
    state = state,
    latitude = latitude,
    longitude = longitude
)

fun LocationModel.toEntity(): Location = Location(
    name = name?.replaceFirstChar { it.uppercase() } ?: "Unknown",
    country = country ?: "",
    state = state,
    latitude = latitude,
    longitude = longitude
)
