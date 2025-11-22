package com.example.outdoorsy.data.repository

import com.example.outdoorsy.data.local.dao.ActivityLogDao
import com.example.outdoorsy.domain.model.ActivityLog
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ActivityLogRepositoryImpl @Inject constructor(
    private val activityLogDao: ActivityLogDao
): ActivityLogRepository {
    override fun getAllActivityLogs(): Flow<List<ActivityLog>> = activityLogDao.getAll().map {
        entityList -> entityList.map { entity -> entity.toDomain() }
    }

    override fun saveActivityLog(activityLog: ActivityLog) {
        activityLogDao.insertActivityLog(activityLog.toEntity())
    }

    override fun deleteActivityLog(activityLog: ActivityLog) {
        activityLogDao.deleteActivityLog(activityLog.toEntity())
    }
}