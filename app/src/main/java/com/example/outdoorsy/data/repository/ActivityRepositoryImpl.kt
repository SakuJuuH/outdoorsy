package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.domain.model.Activity
import com.example.outdoorsy.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityRepositoryImpl @Inject constructor(
    private val activityDao: ActivityDao
) : ActivityRepository {
    private object RecommendationsData {
        var clothingItems: List<String> = emptyList()
    }

    override fun getClothingItems(): List<String> = RecommendationsData.clothingItems

    override fun setClothingItems(items: List<String>) {
        RecommendationsData.clothingItems = items
    }

    override fun getAllActivities(): Flow<List<Activity>> =
        activityDao.getAll().map { entityList ->
            entityList.map { entity -> entity.toDomain() }
        }

    override fun getActivityById(id: Int): Flow<Activity> =
        activityDao.getById(id = id).map { entity ->
            entity.toDomain()
        }

    override fun saveActivity(activity: Activity) {
        activityDao.insertActivity(activity.toEntity())
    }

    override fun deleteActivity(activity: Activity) {
        activityDao.deleteActivity(activity.toEntity())
    }
}