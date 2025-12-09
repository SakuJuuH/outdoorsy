package com.example.outdoorsy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.outdoorsy.domain.model.Location

@Entity(
    tableName = "location",
    indices = [Index(value = ["name"], unique = true)]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val country: String = "",
    val state: String? = null,
    val latitude: Double,
    val longitude: Double
) {
    fun toDomain(): Location = Location(
        name = name.replaceFirstChar { it.uppercase() },
        country = country,
        state = state,
        latitude = latitude,
        longitude = longitude
    )
}
