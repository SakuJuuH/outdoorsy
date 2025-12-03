package com.example.outdoorsy.ui.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.activity.components.EditableFilteringInput
import com.example.outdoorsy.ui.activity.components.RecommendationCard
import com.example.outdoorsy.ui.activity.components.TimePickerField
import com.example.outdoorsy.ui.components.ScreenTitle
import com.example.outdoorsy.ui.theme.WeatherAppTheme

@Composable
fun ActivityScreen(modifier: Modifier = Modifier, viewModel: ActivityViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val isSearchEnabled = uiState.selectedLocation != null &&
        uiState.selectedActivity != null

    LazyColumn(modifier = modifier) {
        item {
            ScreenTitle(
                title = stringResource(R.string.activity_screen_title)
            )
        }

        item {
            EditableFilteringInput(
                options = uiState.locations,
                label = stringResource(id = R.string.activity_screen_location_label),
                prompt = stringResource(id = R.string.activity_screen_location_prompt),
                selectedText = uiState.selectedLocation ?: "",
                onValueSelected = viewModel::updateLocation,
                onDeleteOption = viewModel::deleteLocation
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            EditableFilteringInput(
                options = uiState.activities.map { activity -> activity.name },
                label = stringResource(id = R.string.activity_screen_activity_label),
                prompt = stringResource(id = R.string.activity_screen_activity_prompt),
                selectedText = uiState.selectedActivity?.name ?: "",
                onValueSelected = viewModel::updateActivity,
                onDeleteOption = viewModel::deleteActivity
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimePickerField(
                    label = stringResource(id = R.string.activity_screen_start_time_label),
                    prompt = stringResource(id = R.string.activity_screen_time_prompt),
                    selectedTime = uiState.selectedStartTime,
                    onTimeSelected = { newStartTime ->
                        viewModel.updateStartTime(newStartTime, uiState.selectedEndTime)
                    },
                    modifier = Modifier.weight(1f)
                )

                TimePickerField(
                    label = stringResource(id = R.string.activity_screen_end_time_label),
                    prompt = stringResource(id = R.string.activity_screen_time_prompt),
                    selectedTime = uiState.selectedEndTime,
                    onTimeSelected = { newEndTime ->
                        viewModel.updateEndTime(newEndTime, uiState.selectedStartTime)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (uiState.timeRangeErrorId != null) {
            item {
                Text(
                    text = stringResource(uiState.timeRangeErrorId!!),
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
                onClick = { viewModel.performSearch() },
                enabled = isSearchEnabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(stringResource(id = R.string.activity_screen_search_button))
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
                        modifier = Modifier.size(64.dp),
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
                    text = stringResource(R.string.activity_screen_generic_error),
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
            val answer = uiState.aiAnswer!!

            item {
                RecommendationCard(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.activity_screen_suitability),
                    suitabilityLabel = answer.suitabilityLabel,
                    suitabilityScore = answer.suitabilityScore,
                    items = answer.suitabilityInfo
                )
            }

            item {
                RecommendationCard(
                    icon = Icons.Default.Cloud,
                    title = stringResource(R.string.activity_screen_weather_tips),
                    items = answer.weatherTips
                )
            }

            item {
                RecommendationCard(
                    icon = Icons.Default.Checkroom,
                    title = stringResource(R.string.activity_screen_clothing_tips),
                    items = answer.clothingTips
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
        ActivityScreen()
    }
}
