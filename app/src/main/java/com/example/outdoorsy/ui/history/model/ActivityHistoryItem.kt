package com.example.outdoorsy.ui.history.model

import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDateTime

data class ActivityHistoryItem(
    val activityName: String,
    val activityIcon: ImageVector,
    val location: String,
    val city: String,
    val state: String,
    val timeRange: String,
    val date: String,
    val suitabilityLabel: String,
    val suitabilityScore: Int,
    val startDateTime: LocalDateTime
)
