package com.example.outdoorsy.ui.activity.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditableFilteringInput(
    options: List<String>,
    label: String,
    prompt: String,
    selectedText: String,
    onValueSelected: (String) -> Unit,
    onDeleteOption: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(selectedText) }

    val maxItems = 5
    val filteredOptions = options.filter { it.contains(text, ignoreCase = true) }
    val limitedOptions = filteredOptions.take(maxItems)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = text,
                onValueChange = { newValue ->
                    text = newValue
                    onValueSelected(newValue)
                },
                placeholder = { Text(prompt) },
                singleLine = true,
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        expanded = focusState.isFocused
                    },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            ExposedDropdownMenu(
                expanded = expanded && (text.isNotBlank() || filteredOptions.isNotEmpty()),
                onDismissRequest = { expanded = false },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                if (limitedOptions.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = R.string.no_matches_found),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = { expanded = false }
                    )
                } else {
                    limitedOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(option)
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete Option",
                                        modifier = Modifier.clickable {
                                            onDeleteOption(option)
                                        }
                                    )
                                }
                            },
                            onClick = {
                                text = option
                                onValueSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
