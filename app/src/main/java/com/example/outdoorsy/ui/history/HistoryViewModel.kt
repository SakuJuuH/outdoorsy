package com.example.outdoorsy.ui.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.domain.model.Activity
import com.example.outdoorsy.domain.model.ActivityLog
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import com.example.outdoorsy.domain.repository.ActivityRepository
import com.example.outdoorsy.ui.history.model.ActivityHistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for managing activity history data.
 * Combines activity logs with activity metadata to create UI-ready history items.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    activityLogRepository: ActivityLogRepository,
    activityRepository: ActivityRepository
) : ViewModel() {
    /**
     * Flow of activity history items, combining logs with activity metadata.
     * Sorted by most recent first (descending by startDateTime).
     */
    val historyItems = combine(
        activityLogRepository.getAllActivityLogs(),
        activityRepository.getAllActivities()
    ) { logs, activities ->
        // Create lookup map for efficient activity metadata retrieval
        val activityMap = activities.associateBy { it.name }
        // Transform logs to UI models and sort by most recent first
        logs.map { log -> log.toActivityHistoryItem(activityMap) }
            .sortedByDescending { it.startDateTime }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Transforms an ActivityLog domain model into an ActivityHistoryItem UI model.
     * Formats dates/times and enriches with activity metadata (name, icon).
     */
    private fun ActivityLog.toActivityHistoryItem(
        activityMap: Map<String, Activity>
    ): ActivityHistoryItem {
        // Look up activity metadata, fallback to generic name if not found
        val activity = activityMap[activityName]
        val activityName = activity?.name ?: "Activity"

        // Parse location string into city and state components
        val locationParts = location.split(", ")
        val city = locationParts.getOrElse(0) { location }
        val state = locationParts.getOrElse(1) { "" }

        // Format time range (e.g., "9:00 AM - 11:00 AM")
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val timeRange =
            "${startDateTime.format(timeFormatter)} - ${endDateTime.format(timeFormatter)}"

        // Format date (e.g., "Dec 14, 2025")
        val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        val date = startDateTime.format(dateFormatter)

        return ActivityHistoryItem(
            activityName = activityName,
            activityIcon = getActivityIcon(activityName),
            location = location,
            city = city,
            state = state,
            timeRange = timeRange,
            date = date,
            suitabilityLabel = suitabilityLabel,
            suitabilityScore = suitabilityScore,
            startDateTime = startDateTime
        )
    }

    /**
     * Maps activity names to Material Design icons.
     * Uses case-insensitive matching with fallback to Terrain icon.
     */
    private fun getActivityIcon(activityName: String): ImageVector =
        when (activityName.lowercase()) {
            "cycling", "bike" -> Icons.AutoMirrored.Filled.DirectionsBike
            "beach", "beach activities" -> Icons.Filled.Waves
            "photography" -> Icons.Filled.PhotoCamera
            "hiking" -> Icons.Outlined.Terrain
            "running", "jogging" -> Icons.AutoMirrored.Outlined.DirectionsRun
            "dog walking", "walking" -> Icons.Filled.Pets
            else -> Icons.Outlined.Terrain // Default icon for unknown activities
        }
}
