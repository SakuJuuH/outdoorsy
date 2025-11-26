package com.example.outdoorsy.data.test

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.outlined.Terrain
import com.example.outdoorsy.ui.history.ActivityHistoryItem
import com.example.outdoorsy.ui.history.ConditionRating

object ActivityHistoryData {
    val historyItems: List<ActivityHistoryItem> = listOf(
        ActivityHistoryItem(
            activityName = "Cycling",
            activityIcon = Icons.AutoMirrored.Filled.DirectionsBike,
            location = "San Francisco, CA",
            city = "San Francisco",
            state = "CA",
            timeRange = "8:00 AM - 11:00 AM",
            date = "Oct 29, 2025",
            condition = ConditionRating.EXCELLENT
        ),
        ActivityHistoryItem(
            activityName = "Beach Activities",
            activityIcon = Icons.Filled.Waves,
            location = "Los Angeles, CA",
            city = "Los Angeles",
            state = "CA",
            timeRange = "11:00 AM - 5:00 PM",
            date = "Oct 28, 2025",
            condition = ConditionRating.VERY_GOOD
        ),
        ActivityHistoryItem(
            activityName = "Photography",
            activityIcon = Icons.Filled.PhotoCamera,
            location = "New York, NY",
            city = "New York",
            state = "NY",
            timeRange = "6:00 PM - 7:30 PM",
            date = "Oct 27, 2025",
            condition = ConditionRating.EXCELLENT
        ),
        ActivityHistoryItem(
            activityName = "Hiking",
            activityIcon = Icons.Outlined.Terrain,
            location = "San Francisco, CA",
            city = "San Francisco",
            state = "CA",
            timeRange = "9:00 AM - 3:00 PM",
            date = "Oct 26, 2025",
            condition = ConditionRating.GOOD
        ),
        ActivityHistoryItem(
            activityName = "Running/Jogging",
            activityIcon = Icons.AutoMirrored.Outlined.DirectionsRun,
            location = "New York, NY",
            city = "New York",
            state = "NY",
            timeRange = "6:00 AM - 9:00 AM",
            date = "Oct 25, 2025",
            condition = ConditionRating.VERY_GOOD
        ),
        ActivityHistoryItem(
            activityName = "Dog Walking",
            activityIcon = Icons.Filled.Pets,
            location = "Los Angeles, CA",
            city = "Los Angeles",
            state = "CA",
            timeRange = "7:00 AM - 10:00 AM",
            date = "Oct 24, 2025",
            condition = ConditionRating.GOOD
        )
    )
}
