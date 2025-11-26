package com.example.outdoorsy.domain.repository

import com.example.outdoorsy.domain.model.ActivityLog
import kotlinx.coroutines.flow.Flow

interface ActivityLogRepository {
    fun getAllActivityLogs(): Flow<List<ActivityLog>>

    suspend fun saveActivityLog(activityLog: ActivityLog)

    suspend fun deleteActivityLog(activityLog: ActivityLog)
}