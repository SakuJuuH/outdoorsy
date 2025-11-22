package com.example.outdoorsy.domain.model

import com.example.outdoorsy.data.local.entity.ActivityLog as ActivityLogEntity
import java.time.LocalDateTime

data class ActivityLog(
    val location: String,
    val activityId: Int,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val suitabilityLabel: String,
    val suitabilityScore: Int
) {
    fun toEntity(): ActivityLogEntity = ActivityLogEntity(
        location = location,
        activityId = activityId,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        suitabilityLabel = suitabilityLabel,
        suitabilityScore = suitabilityScore
    )
}
