package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.data.test.ActivitiesData
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.ui.theme.spacing
import com.example.outdoorsy.viewmodel.HistoryViewModel

@Composable
fun SearchScreen(modifier: Modifier = Modifier, viewModel: HistoryViewModel = viewModel()) {
    var query by rememberSaveable { mutableStateOf("") }
    val history by viewModel.recentSearches.collectAsState()
    SearchScreenContent(
        modifier = modifier,
        query = query,
        onQueryChange = { query = it },
        onSubmit = {
            if (query.isNotBlank()) {
                viewModel.submitQuery(query)
                query = ""
            }
        },
        history = history,
        onHistoryClick = { clicked -> query = clicked },
        onHistoryRemove = { toRemove -> viewModel.removeQuery(toRemove) }
    )
}

@Composable
private fun SearchScreenContent(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmit: () -> Unit,
    history: List<String>,
    onHistoryClick: (String) -> Unit,
    onHistoryRemove: (String) -> Unit
) {
    val suggestions = remember(query) {
        if (query.isBlank()) {
            emptyList()
        } else {
            ActivitiesData.activities.filter {
                it.contains(query, ignoreCase = true)
            }.take(10)
        }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing(3))
    ) {
        OutlinedTextField(
            value = query,
            label = { Text(text = stringResource(id = R.string.search_screen_content_label)) },
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = onSubmit) {
                    Icon(imageVector = Icons.Outlined.History, contentDescription = "Submit search")
                }
            },
            shape = MaterialTheme.shapes.medium
        )

        if (suggestions.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.search_screen_content_suggestions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing(1))
            ) {
                items(suggestions) { item ->
                    HistoryRow(
                        text = item,
                        onClick = { onHistoryClick(item) },
                        onRemove = {}
                    )
                }
            }
        }

        if (history.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.search_screen_content_recent),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = MaterialTheme.spacing(8)),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing(2))
            ) {
                items(history) { item ->
                    HistoryRow(
                        text = item,
                        onClick = { onHistoryClick(item) },
                        onRemove = { onHistoryRemove(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(text: String, onClick: () -> Unit, onRemove: () -> Unit) {
    androidx.compose.material3.ListItem(
        headlineContent = { Text(text) },
        leadingContent = {
            Icon(imageVector = Icons.Outlined.History, contentDescription = null)
        },
        trailingContent = {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = stringResource(
                        id = R.string.search_screen_history_row_button
                    )
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    WeatherAppTheme {
        SearchScreenContent(
            query = "",
            onQueryChange = {},
            onSubmit = {},
            history = listOf("Berlin", "Tokyo", "San Francisco"),
            onHistoryClick = {},
            onHistoryRemove = {}
        )
    }
}