package com.example.outdoorsy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import com.example.outdoorsy.domain.model.ActivityLog

// TODO: Uncomment ForeignKey logic once the Activity entity is properly configured
@Entity(
    tableName = "activity_log",
//    foreignKeys = [
//        ForeignKey(
//            entity = Activity::class,
//            parentColumns = ["id"],
//            childColumns = ["activityId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
//    indices = [Index(value = ["activityId"])]
)
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val location: String,
    val activityId: Int,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val suitabilityLabel: String,
    val suitabilityScore: Int
) {
    fun toDomain(): ActivityLog= ActivityLog(
        location = location,
        activityId = activityId,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        suitabilityLabel = suitabilityLabel,
        suitabilityScore = suitabilityScore
    )
}
