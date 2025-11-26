package com.example.outdoorsy.domain.model

import com.example.outdoorsy.data.local.entity.ActivityLogEntity
import java.time.LocalDateTime

data class ActivityLog(
    val location: String,
    val activityName: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val suitabilityLabel: String,
    val suitabilityScore: Int
) {
    fun toEntity(activityId: Int): ActivityLogEntity = ActivityLogEntity(
        location = location,
        activityId = activityId,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        suitabilityLabel = suitabilityLabel,
        suitabilityScore = suitabilityScore
    )
}
