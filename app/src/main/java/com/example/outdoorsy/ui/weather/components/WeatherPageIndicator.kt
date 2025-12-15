package com.example.outdoorsy.ui.weather.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.weather.model.WeatherData

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun WeatherPageIndicator(
    pagerState: PagerState,
    locations: List<WeatherData?>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(locations.size) { iteration ->
            val weatherData = locations[iteration] ?: return@repeat
            val isSelected = pagerState.currentPage == iteration
            val isGpsPage = weatherData.isCurrentLocation
            val color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            }

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isGpsPage) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(
                            R.string.current_location
                        ),
                        modifier = Modifier.fillMaxSize(),
                        tint = color
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, CircleShape)
                    )
                }
            }
        }
    }
}
