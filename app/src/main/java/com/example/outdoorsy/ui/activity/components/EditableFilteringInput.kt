package com.example.outdoorsy.ui.activity.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.R

@Composable
internal fun EditableFilteringInput(
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
                onValueSelected(newValue)
            },
            placeholder = { Text(prompt) },
            label = null,
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
                    contentDescription = if (expanded) {
                        stringResource(R.string.activity_screen_hide_options)
                    } else {
                        stringResource(R.string.activity_screen_show_options)
                    },
                    modifier = Modifier.clickable {
                        expanded = !expanded
                        if (expanded) focusRequester.requestFocus()
                    }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface
            )
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
