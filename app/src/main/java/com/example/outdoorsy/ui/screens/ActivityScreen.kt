package com.example.outdoorsy.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.viewmodel.ActivityViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ActivityScreen(modifier: Modifier = Modifier, viewModel: ActivityViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val isSearchEnabled = uiState.selectedLocation.isNotBlank() &&
            uiState.selectedActivity.isNotBlank()

    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = stringResource(id = R.string.activity_screen_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            EditableFilteringInput(
                options = uiState.locations,
                label = stringResource(id = R.string.activity_screen_location_label),
                prompt = stringResource(id = R.string.activity_screen_location_prompt),
                selectedText = uiState.selectedLocation,
                onValueSelected = viewModel::updateLocation
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            EditableFilteringInput(
                options = uiState.activities,
                label = stringResource(id = R.string.activity_screen_activity_label),
                prompt = stringResource(id = R.string.activity_screen_activity_prompt),
                selectedText = uiState.selectedActivity,
                onValueSelected = viewModel::updateActivity
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimePickerField(
                    label = stringResource(id = R.string.activity_screen_time_label),
                    prompt = stringResource(id = R.string.activity_screen_time_prompt),
                    selectedTime = uiState.selectedStartTime,
                    onTimeSelected = { newStartTime ->
                        viewModel.updateStartTime(newStartTime, uiState.selectedEndTime)
                    },
                    modifier = Modifier.weight(1f)
                )

                TimePickerField(
                    label = stringResource(id = R.string.activity_screen_time_label),
                    prompt = stringResource(id = R.string.activity_screen_time_prompt),
                    selectedTime = uiState.selectedEndTime,
                    onTimeSelected = { newEndTime ->
                        viewModel.updateEndTime(newEndTime, uiState.selectedStartTime)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (uiState.timeRangeError != null) {
            item {
                Text(
                    text = uiState.timeRangeError!!,
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
                modifier = Modifier.fillMaxWidth()
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
                    text = "Something went wrong. Please try again",
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
                    title = "Suitability",
                    suitability = answer.suitabilityScore,
                    items = answer.suitabilityInfo
                )
            }

            item {
                RecommendationCard(
                    icon = Icons.Default.Cloud,
                    title = "Weather Tips",
                    items = answer.weatherTips
                )
            }

            item {
                RecommendationCard(
                    icon = Icons.Default.Checkroom,
                    title = "Clothing Tips",
                    items = answer.clothingTips
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EditableFilteringInput(
    options: List<String>,
    label: String,
    prompt: String,
    selectedText: String,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(selectedText) }

    val focusRequester = remember { FocusRequester() }
    val maxItems = 5

    val filteredOptions = options.filter {
        it.contains(text, ignoreCase = true)
    }
    val limitedOptions = filteredOptions.take(maxItems)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue
                expanded = true
            },
            label = { Text(prompt) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        expanded = false
                    }
                },
            shape = MaterialTheme.shapes.medium,
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) {
                        Icons.Default.ArrowDropUp
                    } else {
                        Icons.Default.ArrowDropDown
                    },
                    contentDescription = if (expanded) "Hide options" else "Show options",
                    modifier = Modifier.clickable {
                        expanded = !expanded
                        if (expanded) focusRequester.requestFocus()
                    }
                )
            }
        )

        if (expanded && (text.isNotBlank() || filteredOptions.isNotEmpty())) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = (56 * maxItems).dp)
                ) {
                    if (limitedOptions.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.activity_screen_no_matches),
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 10.dp
                                ),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        items(limitedOptions) { option ->
                            Text(
                                text = option,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        text = option
                                        onValueSelected(option)
                                        expanded = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerField(
    label: String,
    prompt: String,
    selectedTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    val formattedTime = remember(selectedTime) {
        selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = formattedTime,
            onValueChange = {},
            readOnly = true,
            label = { Text(prompt) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        showDialog = true
                        focusRequester.freeFocus()
                    }
                },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.clickable { showDialog = true }
                )
            },
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        if (showDialog) {
            val dialog = TimePickerDialog(
                context,
                { _, hour: Int, minute: Int ->
                    onTimeSelected(LocalTime.of(hour, minute))
                    showDialog = false
                },
                selectedTime.hour,
                selectedTime.minute,
                true
            )
            dialog.setOnDismissListener {
                showDialog = false
            }
            dialog.show()
        }
    }
}

@Composable
fun RecommendationCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    suitability: String? = null,
    items: List<String>
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (suitability != null) {
                    Spacer(modifier = Modifier.width(12.dp))

                    val (bgColor, textColor) = when (suitability.trim().lowercase()) {
                        "very bad"  -> Color(0xFFD32F2F) to Color.White
                        "bad"       -> Color(0xFFF44336) to Color.White
                        "fair"      -> Color(0xFFFFB300) to Color.Black
                        "good"      -> Color(0xFF4CAF50) to Color.White
                        "very good" -> Color(0xFF2E7D32) to Color.White
                        else        -> Color(0xFF9E9E9E) to Color.White
                    }

                    Text(
                        text = suitability,
                        color = textColor,
                        modifier = modifier
                            .background(bgColor, shape = RoundedCornerShape(percent = 50))
                            .padding(horizontal = 12.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("•", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
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
