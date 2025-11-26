package com.example.outdoorsy.domain.repository

import com.example.outdoorsy.domain.model.Activity
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getClothingItems(): List<String>

    fun setClothingItems(items: List<String>)

    fun getAllActivities(): Flow<List<Activity>>

    fun getActivityById(id: Int): Flow<Activity>

    fun saveActivity(activity: Activity)

    fun deleteActivity(activity: Activity)
}