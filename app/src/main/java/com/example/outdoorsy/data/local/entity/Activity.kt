package com.example.outdoorsy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity")
data class Activity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
