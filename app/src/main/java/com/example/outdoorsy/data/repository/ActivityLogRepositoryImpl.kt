package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.dao.ActivityLogDao
import com.example.outdoorsy.domain.model.ActivityLog
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ActivityLogRepositoryImpl @Inject constructor(
    private val activityLogDao: ActivityLogDao,
    private val activityDao: ActivityDao
): ActivityLogRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllActivityLogs(): Flow<List<ActivityLog>> =
        activityLogDao.getAll().flatMapLatest { entityList ->
            combine(
                flowOf(entityList),
                activityDao.getAll()
            ) { activityLogs, activities ->
                activityLogs.map { activityLog ->
                    val activityName = activities.firstOrNull { it.id == activityLog.activityId }?.name ?: ""
                    activityLog.toDomain(activityName)
                }
            }
        }

    override suspend fun saveActivityLog(activityLog: ActivityLog) {
        val activityEntity = activityDao.getByName(activityLog.activityName).firstOrNull()

        if (activityEntity != null) {
            val logEntity = activityLog.toEntity(activityEntity.id)
            activityLogDao.insertActivityLog(logEntity)
        }
    }

    override suspend fun deleteActivityLog(activityLog: ActivityLog) {
        val activityEntity = activityDao.getByName(activityLog.activityName).firstOrNull()
        if (activityEntity != null) {
            val logEntity = activityLog.toEntity(activityEntity.id)
            activityLogDao.deleteActivityLog(logEntity)
        }
    }
}