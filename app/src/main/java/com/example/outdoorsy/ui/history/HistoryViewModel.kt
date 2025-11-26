package com.example.outdoorsy.ui.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.local.dao.ActivityDao
import com.example.outdoorsy.data.local.datastore.SearchHistoryRepository
import com.example.outdoorsy.data.model.ActivityHistoryItem
import com.example.outdoorsy.data.model.ConditionRating
import com.example.outdoorsy.domain.model.ActivityLog
import com.example.outdoorsy.domain.repository.ActivityLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.ui.graphics.vector.ImageVector

@HiltViewModel
class HistoryViewModel @Inject constructor(
    application: Application,
    private val activityLogRepository: ActivityLogRepository,
    private val activityDao: ActivityDao
) : ViewModel() {
    private val searchHistoryRepository = SearchHistoryRepository(application)
    
    val recentSearches = searchHistoryRepository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    
    val historyItems = combine(
        activityLogRepository.getAllActivityLogs(),
        activityDao.getAll()
    ) { logs, activities ->
        val activityMap = activities.associateBy { it.id }
        logs.map { log -> log.toActivityHistoryItem(activityMap) }
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private fun ActivityLog.toActivityHistoryItem(activityMap: Map<Int, com.example.outdoorsy.data.local.entity.Activity>): ActivityHistoryItem {
        val activity = activityMap[activityId]
        val activityName = activity?.name ?: "Activity"
        val locationParts = location.split(", ")
        val city = locationParts.getOrElse(0) { location }
        val state = locationParts.getOrElse(1) { "" }
        
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val timeRange = "${startDateTime.format(timeFormatter)} - ${endDateTime.format(timeFormatter)}"
        
        val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        val date = startDateTime.format(dateFormatter)
        
        val condition = when (suitabilityLabel.lowercase()) {
            "excellent" -> ConditionRating.EXCELLENT
            "very good" -> ConditionRating.VERY_GOOD
            else -> ConditionRating.GOOD
        }
        
        return ActivityHistoryItem(
            id = activityId.toString(),
            activityName = activityName,
            activityIcon = getActivityIcon(activityName),
            location = location,
            city = city,
            state = state,
            timeRange = timeRange,
            date = date,
            condition = condition
        )
    }
    
    private fun getActivityIcon(activityName: String): ImageVector {
        return when (activityName.lowercase()) {
            "cycling", "bike" -> Icons.Filled.DirectionsBike
            "beach", "beach activities" -> Icons.Filled.Waves
            "photography" -> Icons.Filled.PhotoCamera
            "hiking" -> Icons.Outlined.Terrain
            "running", "jogging" -> Icons.Outlined.DirectionsRun
            "dog walking", "walking" -> Icons.Filled.Pets
            else -> Icons.Outlined.Terrain
        }
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
