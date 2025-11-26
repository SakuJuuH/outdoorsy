package com.example.outdoorsy.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.outdoorsy.domain.model.Activity

@Entity(
    tableName = "activity",
    indices = [Index(value = ["name"], unique = true)]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
) {
    fun toDomain(): Activity = Activity(name = name)
}
