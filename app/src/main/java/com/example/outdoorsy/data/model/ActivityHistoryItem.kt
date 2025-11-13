package com.example.outdoorsy.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ActivityHistoryItem(
    val id: String,
    val activityName: String,
    val activityIcon: ImageVector,
    val location: String,
    val city: String,
    val state: String,
    val timeRange: String,
    val date: String,
    val condition: ConditionRating
)

enum class ConditionRating(val displayName: String) {
    EXCELLENT("Excellent"),
    VERY_GOOD("Very Good"),
    GOOD("Good")
}
