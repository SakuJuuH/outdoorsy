package com.example.outdoorsy.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.data.model.ActivityHistoryItem
import com.example.outdoorsy.data.model.ConditionRating
import com.example.outdoorsy.data.test.ActivityHistoryData
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.ui.theme.spacing

@Composable
fun HistoryScreen(modifier: Modifier = Modifier, viewModel: HistoryViewModel = hiltViewModel()) {
    val historyItems = ActivityHistoryData.historyItems

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Title Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing(4), vertical = MaterialTheme.spacing(3))
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing(2)))
            Text(
                text = stringResource(id = R.string.history_screen_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing(1)))
            Text(
                text = stringResource(id = R.string.history_screen_view_previous_activity_search),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        // Activity List
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

@Composable
private fun ActivityHistoryCard(item: ActivityHistoryItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            val (activityIcon, detailsColumn, ratingPill) = createRefs()

            // Pin the Activity Icon to the start
            Icon(
                imageVector = item.activityIcon,
                contentDescription = item.activityName,
                modifier = Modifier
                    .constrainAs(activityIcon) {
                        start.linkTo(parent.start)
                        centerVerticallyTo(parent)
                    }
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Pin the Condition Rating Pill to the end
            ConditionRatingPill(
                condition = item.condition,
                modifier = Modifier.constrainAs(ratingPill) {
                    end.linkTo(parent.end)
                    centerVerticallyTo(parent)
                }
            )

            // Pin the Details Column between the icon and the pill
            Column(
                modifier = Modifier.constrainAs(detailsColumn) {
                    start.linkTo(activityIcon.end, margin = 24.dp)
                    end.linkTo(ratingPill.start, margin = 16.dp)
                    centerVerticallyTo(parent)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(
                    text = item.activityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1 // Prevent wrapping
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing(1)))

                // Location Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                // Time Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.timeRange,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Date",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConditionRatingPill(condition: ConditionRating, modifier: Modifier = Modifier) {
    val backgroundColor = when (condition) {
        ConditionRating.EXCELLENT -> Color(0xFF4CAF50) // Green
        ConditionRating.VERY_GOOD -> Color(0xFF8BC34A) // Light Green
        ConditionRating.GOOD -> Color(0xFF2196F3) // Blue
    }

    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = MaterialTheme.spacing(2), vertical = MaterialTheme.spacing(1)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = condition.displayName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Filled.ExpandMore,
            contentDescription = "Expand",
            modifier = Modifier.size(16.dp),
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    WeatherAppTheme {
        HistoryScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun ActivityHistoryCardPreview() {
    WeatherAppTheme {
        ActivityHistoryCard(
            item = ActivityHistoryData.historyItems.first()
        )
    }
}
