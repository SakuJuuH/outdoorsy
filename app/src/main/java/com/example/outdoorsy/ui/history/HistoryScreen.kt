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
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.ui.theme.spacing

@Composable
fun HistoryScreen(modifier: Modifier = Modifier, viewModel: HistoryViewModel = hiltViewModel()) {
    val historyItems by viewModel.historyItems.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Title
        ScreenTitle(
            title = stringResource(R.string.history_screen_title),
            subtitle = stringResource(R.string.history_screen_view_previous_activity_search)
        )

        // Activity List
        if (historyItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing(2)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.history_screen_no_history),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
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
