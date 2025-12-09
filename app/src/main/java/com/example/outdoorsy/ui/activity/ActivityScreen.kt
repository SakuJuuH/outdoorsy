package com.example.outdoorsy.ui.activity

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.activity.components.DatePickerField
import com.example.outdoorsy.ui.activity.components.EditableFilteringInput
import com.example.outdoorsy.ui.activity.components.HelpTooltip
import com.example.outdoorsy.ui.activity.components.RecommendationCard
import com.example.outdoorsy.ui.activity.components.ShopMessageCard
import com.example.outdoorsy.ui.activity.components.TimePickerField
import com.example.outdoorsy.ui.components.ScreenTitle
import com.example.outdoorsy.ui.navigation.Screen
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ActivityScreen(
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    ActivityScreenContent(
        modifier = modifier,
        uiState = uiState,
        onUpdateActivity = viewModel::updateActivity,
        onDeleteActivity = viewModel::deleteActivity,
        onUpdateLocation = viewModel::updateLocation,
        onUpdateShowDialog = viewModel::updateShowDialog,
        onUpdateNewActivityName = viewModel::updateNewActivityName,
        onAddActivity = viewModel::addActivity,
        onUpdateStartDateTime = viewModel::updateStartDateTime,
        onUpdateEndDateTime = viewModel::updateEndDateTime,
        onPerformSearch = viewModel::performSearch,
        onNavigateToShop = {
            navController.navigate(Screen.AppNav.Shopping.route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) { saveState = true }
            }
        }
    )
}

@Composable
internal fun ActivityScreenContent(
    modifier: Modifier = Modifier,
    uiState: ActivityUiState,
    onUpdateActivity: (String) -> Unit,
    onDeleteActivity: (String) -> Unit,
    onUpdateLocation: (String) -> Unit,
    onUpdateShowDialog: (Boolean) -> Unit,
    onUpdateNewActivityName: (String) -> Unit,
    onAddActivity: (String) -> Unit,
    onUpdateStartDateTime: (LocalDate, LocalTime, LocalDate, LocalTime) -> Unit,
    onUpdateEndDateTime: (LocalDate, LocalTime, LocalDate, LocalTime) -> Unit,
    onPerformSearch: () -> Unit,
    onNavigateToShop: () -> Unit
) {
    val isSearchEnabled = uiState.selectedLocation != null &&
        uiState.selectedActivity != null

    LazyColumn(modifier = modifier) {
        item {
            ScreenTitle(
                title = stringResource(R.string.activity_screen_title),
                subtitle = stringResource(R.string.activity_screen_subtitle)
            )
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EditableFilteringInput(
                    modifier = Modifier.weight(1f),
                    options = uiState.activities.map { activity -> activity.name },
                    label = stringResource(id = R.string.activity_label),
                    prompt = stringResource(id = R.string.activity_prompt),
                    selectedText = uiState.selectedActivity?.name ?: "",
                    onValueSelected = onUpdateActivity,
                    onDeleteOption = onDeleteActivity,
                    noOptionsText = stringResource(id = R.string.no_activities)
                )
                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = { onUpdateShowDialog(true) },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = 8.dp)
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_activity_title),
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EditableFilteringInput(
                    modifier = Modifier.weight(1f),
                    options = uiState.locations.map { location ->
                        location.name ?: "Unknown: ${location.latitude} ${location.longitude}"
                    },
                    label = stringResource(id = R.string.location_label),
                    prompt = stringResource(id = R.string.location_prompt),
                    selectedText = uiState.selectedLocation?.name ?: "",
                    onValueSelected = onUpdateLocation,
                    noOptionsText = stringResource(id = R.string.no_locations)
                )
                Spacer(modifier = Modifier.width(12.dp))
                HelpTooltip(stringResource(id = R.string.location_tooltip))
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.showActivityDialog) {
            item {
                AlertDialog(
                    onDismissRequest = { onUpdateShowDialog(false) },
                    title = { Text(text = stringResource(id = R.string.add_activity_title)) },
                    text = {
                        TextField(
                            value = uiState.newActivityName,
                            onValueChange = { onUpdateNewActivityName(it) },
                            label = { Text(stringResource(id = R.string.add_activity_prompt)) }
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onAddActivity(uiState.newActivityName)
                                onUpdateNewActivityName("")
                                onUpdateShowDialog(false)
                            }
                        ) {
                            Text(stringResource(id = R.string.confirm_button_label))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { onUpdateShowDialog(false) }) {
                            Text(stringResource(id = R.string.cancel_button_label))
                        }
                    }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DatePickerField(
                    label = stringResource(id = R.string.start_time_label),
                    selectedDate = uiState.selectedStartDate,
                    onDateSelected = { newDate ->
                        onUpdateStartDateTime(
                            newDate,
                            uiState.selectedStartTime,
                            uiState.selectedEndDate,
                            uiState.selectedEndTime
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                DatePickerField(
                    label = stringResource(id = R.string.activity_screen_end_date_label),
                    selectedDate = uiState.selectedEndDate,
                    onDateSelected = { newDate ->
                        onUpdateEndDateTime(
                            newDate,
                            uiState.selectedEndTime,
                            uiState.selectedStartDate,
                            uiState.selectedStartTime
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimePickerField(
                    label = stringResource(id = R.string.activity_screen_start_time_label),
                    selectedTime = uiState.selectedStartTime,
                    onTimeSelected = { newTime ->
                        onUpdateStartDateTime(
                            uiState.selectedStartDate,
                            newTime,
                            uiState.selectedEndDate,
                            uiState.selectedEndTime
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                TimePickerField(
                    label = stringResource(id = R.string.end_time_label),
                    selectedTime = uiState.selectedEndTime,
                    onTimeSelected = { newTime ->
                        onUpdateEndDateTime(
                            uiState.selectedEndDate,
                            newTime,
                            uiState.selectedStartDate,
                            uiState.selectedStartTime
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (uiState.timeRangeErrorId != null) {
            item {
                Text(
                    text = stringResource(uiState.timeRangeErrorId),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        item {
            Button(
                onClick = { onPerformSearch() },
                enabled = isSearchEnabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(stringResource(id = R.string.search_button_label))
            }
        }

        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                            .testTag("loading_indicator"),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 6.dp
                    )
                }
            }
        }

        if (uiState.searchPerformed == false) {
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.generic_error),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                )
            }
        }

        if (uiState.searchPerformed == true && uiState.aiAnswer != null) {
            val answer = uiState.aiAnswer

            item {
                RecommendationCard(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.suitability),
                    suitabilityLabel = answer.suitabilityLabel,
                    suitabilityScore = answer.suitabilityScore,
                    items = answer.suitabilityInfo
                )
            }

            item {
                RecommendationCard(
                    icon = Icons.Default.Cloud,
                    title = stringResource(R.string.weather_tips),
                    items = answer.weatherTips
                )
            }

            item {
                RecommendationCard(
                    icon = Icons.Default.Checkroom,
                    title = stringResource(R.string.clothing_tips),
                    items = answer.clothingTips
                )
            }

            item {
                ShopMessageCard(
                    onNavigateToShop = onNavigateToShop
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun ActivityScreenPreview() {
    WeatherAppTheme {
        ActivityScreen(
            navController = rememberNavController()
        )
    }
}
