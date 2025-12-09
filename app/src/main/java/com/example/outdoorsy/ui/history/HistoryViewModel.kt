package com.example.outdoorsy.ui.history

import android.app.Application
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
import com.example.outdoorsy.data.local.datastore.SearchHistoryRepository
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
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val activityLogRepository: ActivityLogRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {
    private val searchHistoryRepository = SearchHistoryRepository(application)

    val recentSearches = searchHistoryRepository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val historyItems = combine(
        activityLogRepository.getAllActivityLogs(),
        activityRepository.getAllActivities()
    ) { logs, activities ->
        val activityMap = activities.associateBy { it.name }
        logs.map { log -> log.toActivityHistoryItem(activityMap) }
            .sortedByDescending { it.startDateTime } // Sort from youngest to oldest
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private fun ActivityLog.toActivityHistoryItem(
        activityMap: Map<String, Activity>
    ): ActivityHistoryItem {
        val activity = activityMap[activityName]
        val activityName = activity?.name ?: "Activity"
        val locationParts = location.split(", ")
        val city = locationParts.getOrElse(0) { location }
        val state = locationParts.getOrElse(1) { "" }

        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val timeRange =
            "${startDateTime.format(timeFormatter)} - ${endDateTime.format(timeFormatter)}"

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

    private fun getActivityIcon(activityName: String): ImageVector =
        when (activityName.lowercase()) {
            "cycling", "bike" -> Icons.AutoMirrored.Filled.DirectionsBike
            "beach", "beach activities" -> Icons.Filled.Waves
            "photography" -> Icons.Filled.PhotoCamera
            "hiking" -> Icons.Outlined.Terrain
            "running", "jogging" -> Icons.AutoMirrored.Outlined.DirectionsRun
            "dog walking", "walking" -> Icons.Filled.Pets
            else -> Icons.Outlined.Terrain
        }

    fun submitQuery(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.addQuery(query)
        }
    }

    fun removeQuery(query: String) {
        viewModelScope.launch {
            searchHistoryRepository.removeQuery(query)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            searchHistoryRepository.clear()
        }
    }
}
