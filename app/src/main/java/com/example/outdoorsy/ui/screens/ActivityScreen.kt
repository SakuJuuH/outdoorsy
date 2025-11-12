package com.example.outdoorsy.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.viewmodel.ActivityViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ActivityScreen(modifier: Modifier = Modifier, viewModel: ActivityViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    val isSearchEnabled = uiState.selectedLocation.isNotBlank() &&
        uiState.selectedActivity.isNotBlank()

    Column(modifier = modifier) {
        Text(
            text = "Activity Planner",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditableFilteringInput(
            options = uiState.locations,
            label = "Select Location",
            prompt = "Choose a location...",
            selectedText = uiState.selectedLocation,
            onValueSelected = viewModel::updateLocation
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditableFilteringInput(
            options = uiState.activities,
            label = "Select Activity",
            prompt = "Choose an activity...",
            selectedText = uiState.selectedActivity,
            onValueSelected = viewModel::updateActivity
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimePickerField(
            label = "Select Time",
            prompt = "Choose a time...",
            selectedTime = uiState.selectedTime,
            onTimeSelected = viewModel::updateTime
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.performSearch() },
            enabled = isSearchEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        // TODO: Add search output
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
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Hide options" else "Show options",
                    modifier = Modifier.clickable {
                        expanded = !expanded
                        if (expanded) focusRequester.requestFocus()
                    }
                )
            },
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
                                text = "No matches found",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
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
    onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val formattedTime = remember(selectedTime) {
        selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Column {
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
                .clickable { showDialog = true },
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
            TimePickerDialog(
                context,
                { _, hour: Int, minute: Int ->
                    onTimeSelected(LocalTime.of(hour, minute))
                    showDialog = false
                },
                selectedTime.hour,
                selectedTime.minute,
                true
            ).show()
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
