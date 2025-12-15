package com.example.outdoorsy.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.components.ScreenTitle
import com.example.outdoorsy.ui.history.components.ActivityHistoryCard
import com.example.outdoorsy.ui.history.model.ActivityHistoryItem
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.ui.theme.spacing

/**
 * Main composable for the History screen.
 * Displays a list of past activity searches with their details.
 */
@Composable
fun HistoryScreen(modifier: Modifier = Modifier, viewModel: HistoryViewModel = hiltViewModel()) {
    // Collect history items from ViewModel state
    val historyItems by viewModel.historyItems.collectAsState()
    HistoryScreenContent(historyItems = historyItems, modifier = modifier)
}

/**
 * Stateless content composable for the History screen.
 * Separated from HistoryScreen for easier testing and preview support.
 */
@Composable
internal fun HistoryScreenContent(
    historyItems: List<ActivityHistoryItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Screen header with title and subtitle
        ScreenTitle(
            title = stringResource(R.string.activity_history),
            subtitle = stringResource(R.string.view_previous_activity_search)
        )

        // Display empty state or list of history items
        if (historyItems.isEmpty()) {
            // Empty state: Show message when no history exists
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing(2)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.no_activity_history),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Scrollable list of activity history cards, sorted by most recent first
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = MaterialTheme.spacing(1),
                    vertical = MaterialTheme.spacing(2)
                ),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing(2))
            ) {
                items(historyItems) { item ->
                    ActivityHistoryCard(item = item)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    WeatherAppTheme {
        HistoryScreen()
    }
}
