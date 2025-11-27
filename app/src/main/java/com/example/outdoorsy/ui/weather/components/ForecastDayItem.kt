package com.example.outdoorsy.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.example.outdoorsy.ui.weather.model.DailyForecast

@Composable
internal fun ForecastDayItem(dailyForecast: DailyForecast, modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        val (dayText, conditionRow, highTempText, lowTempText) = createRefs()

        val startConditionGuideline = createGuidelineFromStart(0.22f)
        val startLowTempGuideline = createGuidelineFromStart(0.8f)

        Text(
            text = dailyForecast.day,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(dayText) {
                start.linkTo(parent.start)
                centerVerticallyTo(parent)
                end.linkTo(startConditionGuideline, margin = 16.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = "${dailyForecast.high}°",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(highTempText) {
                end.linkTo(parent.end)
                centerVerticallyTo(parent)
            }
        )

        Text(
            text = "${dailyForecast.low}°",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.constrainAs(lowTempText) {
                start.linkTo(startLowTempGuideline)
                centerVerticallyTo(parent)
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.constrainAs(conditionRow) {
                start.linkTo(startConditionGuideline)
                end.linkTo(startLowTempGuideline, margin = 16.dp)
                width = Dimension.fillToConstraints
                centerVerticallyTo(parent)
            }
        ) {
            if (dailyForecast.icon.isNotBlank()) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${dailyForecast.icon}@2x.png",
                    contentDescription = dailyForecast.condition,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = dailyForecast.condition,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = dailyForecast.condition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
